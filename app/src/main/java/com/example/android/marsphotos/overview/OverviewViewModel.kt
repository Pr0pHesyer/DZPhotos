/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.marsphotos.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.DZApi
import com.example.android.marsphotos.network.DZPhoto
import kotlinx.coroutines.launch

enum class DZApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<DZApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<DZApiStatus> = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsPhoto
    // with new values
    private val _photos = MutableLiveData<List<DZPhoto>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val photos: LiveData<List<DZPhoto>> = _photos

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getDZPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [DZPhoto] [List] [LiveData].
     */
    private fun getDZPhotos() {

        viewModelScope.launch {
            _status.value = DZApiStatus.LOADING
            try {
                _photos.value = listOf(DZApi.retrofitService.getPhotos())
                _status.value = DZApiStatus.DONE
            } catch (e: Exception) {
                _status.value = DZApiStatus.ERROR
                _photos.value = listOf()
            }
        }
    }

    fun onRefresh(){
        _isLoading.value = true
        getDZPhotos()
        _isLoading.value = false
    }
}
