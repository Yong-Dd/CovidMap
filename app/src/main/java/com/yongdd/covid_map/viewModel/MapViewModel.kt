package com.yongdd.covid_map.viewModel

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.yongdd.covid_map.R
import com.yongdd.covid_map.model.data.Center
import com.yongdd.covid_map.model.data.MarkerColor
import com.yongdd.covid_map.model.repository.CenterRepository
import com.yongdd.covid_map.utils.Event
import com.yongdd.covid_map.utils.SendAlert
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.ShowAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: CenterRepository
) : ViewModel() {
    // dialog 등 띄우는 용
    private val _alertEvent = MutableLiveData<Event<SendAlert>>()
    val alertEvent : LiveData<Event<SendAlert>> get() = _alertEvent

    // 이벤트 전달 용
    private val _viewEvent = MutableLiveData<Event<SendToView>>()
    val viewEvent : LiveData<Event<SendToView>> get() = _viewEvent

    // 클릭한 마커의 센터 정보 (null일 경우 클릭X)
    private val _clickedCenter = MutableStateFlow<Center?>(null)
    val clickedCenter : StateFlow<Center?> get() = _clickedCenter

    // 센터 리스트
    private val _centerList = MutableStateFlow<MutableList<Center>>(mutableListOf())
    val centerList : StateFlow<MutableList<Center>> get() = _centerList

    private val MAP_VM = "mapViewModel" // log tag

    val API_ERROR = "apiError"
    fun getListAndAddMarkers(naverMap: NaverMap) {
        viewModelScope.launch {
            if(centerList.value.isNullOrEmpty()) {
               repository.centerListFromDao
                   .retryWhen { cause, attempt ->
                       cause is SQLiteException && attempt < 2
                   }
                   .catch {
                       _alertEvent.value = Event(SendAlert.ShowOneChoiceAlert(
                           title = "안내",
                           message = "정보를 가지고 오지 못했습니다😥/n잠시 후 다시 시도해 주세요!",
                           positiveText = "확인"){})
                       Log.d(MAP_VM,"centerListFromDao error ${it.printStackTrace()}")
                   }
                   .collect{list ->
                   _centerList.value = list.toMutableList()
               }
            }

            centerList.value?.forEachIndexed { index, center ->
                if(index==0) moveCameraUpdate(naverMap,center.lat.toDouble(),center.lng.toDouble())
                addMarker(naverMap, center)
            }
        }
    }

    private fun addMarker(naverMap: NaverMap, center: Center) {
        viewModelScope.launch {
            val marker = Marker()
            marker.position = LatLng(center.lat.toDouble(),center.lng.toDouble())
            marker.icon = markerImage(center.markerColor)
            marker.map = naverMap
            marker.width = 80
            marker.height = 105
            marker.onClickListener = Overlay.OnClickListener { _ ->
                moveCameraUpdate(naverMap,center.lat.toDouble(), center.lng.toDouble())

                val beforeClickedCenter = clickedCenter.value
                if(beforeClickedCenter?.id == center.id)  {
                    clickedCenterChange(null)
                    return@OnClickListener true
                }
                clickedCenterChange(center)

                true
            }
        }
    }

    // 마커 이미지
    private fun markerImage(markerColor: MarkerColor): OverlayImage = when (markerColor) {
        MarkerColor.RED -> OverlayImage.fromResource(R.drawable.marker_red)
        MarkerColor.BLUE -> OverlayImage.fromResource(R.drawable.marker_blue)
        MarkerColor.GREEN -> OverlayImage.fromResource(R.drawable.marker_green)
    }

    // 지도 이동
    fun moveCameraUpdate(naverMap: NaverMap, lat:Double, lng:Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lng))
            .animate(CameraAnimation.Easing, 400)
        naverMap.moveCamera(cameraUpdate)
    }

    fun clickedCenterChange(center: Center?) {
        _clickedCenter.value = center
    }

    fun getLastUpdateText(lastUpdate:String?) = "마지막 업데이트 $lastUpdate"

    // 중앙/권역과 지역 외 다른 것이 있는지 체크 (true : 있음)
    fun getCenterTypeEtcCheck(list:MutableList<Center>?):Boolean {
        return list?.none { it.markerColor == MarkerColor.GREEN } != true
    }

    val GO_TO_CALL = "goToCall"
    fun phoneViewClick(phoneNumber:String) {
        _viewEvent.value = Event(SendToView.SendData(GO_TO_CALL,phoneNumber))
    }

    val MOVE_CURRENT_LOCATION = "moveCurrentLocation"
    fun currentLocationBtnClick() {
        _viewEvent.value = Event(SendToView.SendData(MOVE_CURRENT_LOCATION,0))
    }


}