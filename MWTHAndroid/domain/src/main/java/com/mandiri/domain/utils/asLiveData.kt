package com.mandiri.domain.utils

import io.realm.RealmModel
import io.realm.RealmResults

// Use as
// realmResults.asLiveData() in Kotlin
// new RealmLiveData(realm)  in Java
fun <T: RealmModel> RealmResults<T>.asLiveData() = RealmLiveData<T>(this)