/*
 * Copyright 2018 Tsuyoshi Murakami
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

@file:Suppress("NOTHING_TO_INLINE")

package com.github.tmurakami.aackt.lifecycle

import androidx.annotation.MainThread
import androidx.lifecycle.ChangesOnlySubscriptionImpl
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

@Deprecated("", ReplaceWith("MutableLiveData(value)", "androidx.lifecycle.MutableLiveData"))
@MainThread
inline fun <T> liveData(value: T): LiveData<T> {
    // To save methods count, we prefer MutableLiveData rather than `object : LiveData<T>() {}`.
    return MutableLiveData(value)
}

@Suppress("DEPRECATION")
@Deprecated("", ReplaceWith("subscribe(observer)"))
@MainThread
inline fun <T> LiveData<T>.observe(crossinline observer: (T) -> Unit): Observation =
    subscribe(observer)

/**
 * Adds the given [action] to the receiver. If the receiver already has a value, it will be
 * delivered to the [action].
 *
 * To unsubscribe from the receiver, you should manually call [Subscription.unsubscribe] with the
 * resulting [Subscription].
 */
@MainThread
inline fun <T> LiveData<T>.subscribe(crossinline action: (T) -> Unit): Subscription =
    subscribe(Observer { action(it) })

/**
 * Adds the given [observer] to the receiver. If the receiver already has a value, it will be
 * delivered to the [observer].
 *
 * To unsubscribe from the receiver, you should manually call [Subscription.unsubscribe] with the
 * resulting [Subscription].
 */
@MainThread
fun <T> LiveData<T>.subscribe(observer: Observer<T>): Subscription =
    SubscriptionImpl(this, observer).also { observeForever(observer) }

@Suppress("DEPRECATION")
@Deprecated("", ReplaceWith("subscribeChanges(onChanged)"))
@MainThread
inline fun <T> LiveData<T>.observeChanges(crossinline onChanged: (T) -> Unit): Observation =
    subscribeChanges(onChanged)

/**
 * Adds the given [action] to the receiver. Unlike [subscribe] extension, the cached value won't be
 * delivered to the [action].
 *
 * To unsubscribe from the receiver, you should manually call [Subscription.unsubscribe] with the
 * resulting [Subscription].
 */
// TODO https://issuetracker.google.com/issues/94056118
@MainThread
inline fun <T> LiveData<T>.subscribeChanges(crossinline action: (T) -> Unit): Subscription =
    subscribeChanges(Observer { action(it) })

/**
 * Adds the given [observer] to the receiver. Unlike [subscribe] extension, the cached value won't
 * be delivered to the [observer].
 *
 * To unsubscribe from the receiver, you should manually call [Subscription.unsubscribe] with the
 * resulting [Subscription].
 */
// TODO https://issuetracker.google.com/issues/94056118
@MainThread
fun <T> LiveData<T>.subscribeChanges(observer: Observer<T>): Subscription =
    ChangesOnlySubscriptionImpl(this, observer).also { observeForever(it) }

@Suppress("DEPRECATION")
@Deprecated("", ReplaceWith("subscribe(owner, observer)"))
@MainThread
inline fun <T> LiveData<T>.observe(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
): Observation = subscribe(owner, observer)

/**
 * Adds the given [action] to the receiver. If the receiver already has a value, it will be
 * delivered to the [action].
 *
 * The callback will receive values only while the given [owner] is active. You can manually
 * unsubscribe by calling [Subscription.unsubscribe] with the resulting [Subscription].
 */
@MainThread
inline fun <T> LiveData<T>.subscribe(
    owner: LifecycleOwner,
    crossinline action: (T) -> Unit
): Subscription = subscribe(owner, Observer { action(it) })

/**
 * Adds the given [observer] to the receiver. If the receiver already has a value, it will be
 * delivered to the [observer].
 *
 * The callback will receive values only while the given [owner] is active. You can manually
 * unsubscribe by calling [Subscription.unsubscribe] with the resulting [Subscription].
 */
@MainThread
fun <T> LiveData<T>.subscribe(owner: LifecycleOwner, observer: Observer<T>): Subscription =
    SubscriptionImpl(this, observer).also { observe(owner, observer) }

@Suppress("DEPRECATION")
@Deprecated("", ReplaceWith("subscribeChanges(owner, onChanged)"))
@MainThread
inline fun <T> LiveData<T>.observeChanges(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observation = subscribeChanges(owner, onChanged)

/**
 * Adds the given [action] to the receiver. Unlike [subscribe] extension, the cached value won't be
 * delivered to the [action].
 *
 * The callback will receive values only while the given [owner] is active. You can manually
 * unsubscribe by calling [Subscription.unsubscribe] with the resulting [Subscription].
 */
// TODO https://issuetracker.google.com/issues/94056118
@MainThread
inline fun <T> LiveData<T>.subscribeChanges(
    owner: LifecycleOwner,
    crossinline action: (T) -> Unit
): Subscription = subscribeChanges(owner, Observer { action(it) })

/**
 * Adds the given [observer] to the receiver. Unlike [subscribe] extension, the cached value won't
 * be delivered to the [observer].
 *
 * The callback will receive values only while the given [owner] is active. You can manually
 * unsubscribe by calling [Subscription.unsubscribe] with the resulting [Subscription].
 */
// TODO https://issuetracker.google.com/issues/94056118
@MainThread
fun <T> LiveData<T>.subscribeChanges(
    owner: LifecycleOwner,
    observer: Observer<T>
): Subscription = ChangesOnlySubscriptionImpl(this, observer).also { observe(owner, it) }
