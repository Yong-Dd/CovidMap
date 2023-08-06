package com.yongdd.covid_map.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yongdd.covid_map.model.repository.CenterRepository
import com.yongdd.covid_map.model.data.Center
import com.yongdd.covid_map.model.data.CenterInfo
import com.yongdd.covid_map.utils.Event
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.ShowAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: CenterRepository
) : ViewModel() {

    val INTERNET_CHECK = "internetCheck"
    private val API_TAG = "SPLASH API"

    /*센터 리스트 업데이트 여부
* (기존에 정보를 전체 갖고 있는 경우 업데이트)*/
    private var listUpdate = false

    // dialog 등 띄우는 용
    private val _alertEvent = MutableLiveData<Event<ShowAlert>>()
    val alertEvent : LiveData<Event<ShowAlert>> get() = _alertEvent

    // 이벤트 전달 용
    private val _viewEvent = MutableLiveData<Event<SendToView>>()
    val viewEvent : LiveData<Event<SendToView>> get() = _viewEvent


    // progressBar progress 값
    private val _progress = MutableLiveData(0)
    val progress : LiveData<Int> = _progress

    // 성공 API 개수
    private val _receivedPageCount = MutableLiveData(0)
    val receivedPageCount : LiveData<Int> = _receivedPageCount



    init {
        CoroutineScope(Dispatchers.IO).launch {
            listUpdate = repository.getAllCountDao()>=100
        }

        moveProgressBar(2000,0)
        controlListFromAPI()
    }

    // 페이지별 api 요청
    private fun controlListFromAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            (1..10).asFlow().collect{page ->
                getListFromAPI(page,null)
            }
        }
    }


    val reRequest = mutableMapOf<Int,Int>()  // 재요청한 페이지, 요청 횟수

    // 페이지별 api 요청 처리
    private suspend fun getListFromAPI(page:Int, perPage:Int?) {
        val response = repository.getCenterListFromAPI(page,perPage)

        when(response.isSuccessful) {
            true -> {
                saveData(response.body()?.data)
                updateReceivedPage()
            }
            else -> {
                _viewEvent.value = Event(SendToView.SendData(INTERNET_CHECK,0))

                when(response.code()) {
                    // 서버에 문제가 생긴 경우 -> 1번 재요청
                    500 -> {
                        // 요청 처리한 것으로 넘김 (다시 받아오기 힘든 상황이라 추측) -> 차후 Activity에서 처리
                        if((reRequest[page]?:0)>1){
                            updateReceivedPage()
                            return
                        }

                        getListFromAPI(page, perPage)
                        reRequest[page] = (reRequest[page]?:0) +1
                    }

                    else -> {
                        // 요청 처리한 것으로 넘김 (다시 받아오기 힘든 상황) -> 차후 Activity에서 처리
                        updateReceivedPage()
                    }
                }

                Log.d(API_TAG,"api error $response / page $page")

            }
        }
    }

    // 가져온 정보를 database에 save하는 부분
    private suspend fun saveData(centerLists: List<CenterInfo>?) {
        Log.d(API_TAG,"received api list $centerLists")
        if(centerLists.isNullOrEmpty()) return

        centerLists.asFlow().collect{ centerInfo ->
            val center = Center(
                id = centerInfo.id,
                address = centerInfo.address,
                centerName = centerInfo.centerName,
                centerType = centerInfo.centerType,
                createdAt = centerInfo.createdAt,
                facilityName = centerInfo.facilityName,
                lat = centerInfo.lat,
                lng = centerInfo.lng,
                org = centerInfo.org,
                phoneNumber = centerInfo.phoneNumber,
                sido = centerInfo.sido,
                sigungu = centerInfo.sigungu,
                updatedAt = centerInfo.updatedAt,
                zipCode = centerInfo.zipCode
            )

            CoroutineScope(Dispatchers.IO).launch {
                if(listUpdate) {
                    repository.updateDao(center)
                } else {
                    repository.addCenterDao(center)

                }
            }
        }
    }

    /* 프로그래스바 progress 변경
    * max = 2000,
    * 총 2초 동안 100%로 변경
    * totoalDurationMillis : 최대 값이 주어진 것에 따라 계산 되도록 처리 -기본(2000)
    * processedProgress : 기존에 진행된 값 - 기본(0)
    * / 80% 진행 됐지만, 서버에서 값을 받지 못한 경우 break 후 재시작 시 이어서 하도록 대비 */
    private fun moveProgressBar(totalDurationMillis:Int, processedProgress:Int) {
        val maxProgress = 100 - processedProgress // progress bar의 최대 값 (0 ~ 100)
        val intervalMillis = 1L // 1밀리 초마다 업데이트

        val startTime = System.currentTimeMillis()
        var elapsedTime = 0L

        viewModelScope.launch {
            while (elapsedTime <= totalDurationMillis) {

                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime

                // 진행된 시간을 %로 계산 + 기존에 진행되었던 시간(기본 0)
                val currentProgress = (elapsedTime.toFloat() / totalDurationMillis * maxProgress).toInt()
                updateProgress(currentProgress + processedProgress)

                /* 80% 이상 진행되었고(2000*0.8=1600)
                    && api에서 불러온 전체 페이지를 저장하지 않았을 경우 될 때까지 대기 */
                if(currentProgress >= 80 && (receivedPageCount.value?:0) < 10){
                    break
                }

                delay(intervalMillis)
            }
        }
    }

    // moveProgressBar가 진행되고 있는지 확인 후 멈춰있으면 재시작
    fun checkProcessing() {
        CoroutineScope(Dispatchers.IO).launch {
            // 80 이하면 진행 중 -> 처리할 필요 없음
            if((progress.value?:0) < 80) return@launch

            // 5 Millis 기다려서 변경이 있는지 확인
            val startProgress = progress.value
            delay(5L)
            val endProgress = progress.value

            // vm의 moveProgressBar가 api 처리 대기를 위해 멈춰 있는 상황 -> 다시 진행
            if(startProgress == endProgress){
                moveProgressBar(400, progress.value ?: 0)
            }
        }
    }

    private fun updateProgress(changeProgress:Int) {
        if(changeProgress == progress.value) return
        viewModelScope.launch {
            _progress.value = changeProgress
        }
    }

    // +1씩 증가
    private fun updateReceivedPage() {
        viewModelScope.launch {
            _receivedPageCount.value = _receivedPageCount.value?.plus(1)
        }
    }

}