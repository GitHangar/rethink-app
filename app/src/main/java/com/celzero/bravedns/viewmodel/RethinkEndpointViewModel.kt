/*
 * Copyright 2020 RethinkDNS and its authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.celzero.bravedns.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.celzero.bravedns.database.RethinkDnsEndpointDao
import com.celzero.bravedns.util.Constants
import com.celzero.bravedns.util.Constants.Companion.LIVEDATA_PAGE_SIZE

class RethinkEndpointViewModel(private val rethinkDnsEndpointDao: RethinkDnsEndpointDao) :
        ViewModel() {

    private var list: MutableLiveData<String> = MutableLiveData()
    private var uid: Int = Constants.MISSING_UID

    init {
        list.value = ""
    }

    val rethinkEndpointList = Transformations.switchMap(list, ({ input: String ->
        if (uid == Constants.MISSING_UID) {
            if (input.isBlank()) {
                Pager(PagingConfig(LIVEDATA_PAGE_SIZE)) {
                    rethinkDnsEndpointDao.getRethinkEndpoints()
                }.liveData.cachedIn(viewModelScope)
            } else {
                Pager(PagingConfig(LIVEDATA_PAGE_SIZE)) {
                    rethinkDnsEndpointDao.getRethinkEndpointsByName("%$input%")
                }.liveData.cachedIn(viewModelScope)
            }
        } else {
            Pager(PagingConfig(LIVEDATA_PAGE_SIZE)) {
                rethinkDnsEndpointDao.getAllRethinkEndpoints()
            }.liveData.cachedIn(viewModelScope)
        }

    }))

    fun setFilter(uid: Int, searchText: String = "") {
        this.uid = uid
        list.value = searchText
    }

}
