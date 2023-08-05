package com.yongdd.covid_map.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yongdd.covid_map.model.repository.CenterRepository
import com.yongdd.covid_map.utils.Event
import com.yongdd.covid_map.utils.SendToView
import com.yongdd.covid_map.utils.ShowAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: CenterRepository
) : ViewModel() {
    // dialog 등 띄우는 용
    private val _alertEvent = MutableLiveData<Event<ShowAlert>>()
    val alertEvent : LiveData<Event<ShowAlert>> get() = _alertEvent

    // 이벤트 전달 용
    private val _viewEvent = MutableLiveData<Event<SendToView>>()
    val viewEvent : LiveData<Event<SendToView>> get() = _viewEvent

}