package com.github.tmurakami.aackt.lifecycle

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.MainThread
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Represents a collection of [ViewModel].
 */
@MainThread
class ViewModels(provider: () -> ViewModelProvider) {

    private val instance by lazy(LazyThreadSafetyMode.NONE, provider)

    /**
     * Returns an existing [ViewModel] with the given [key], or creates a new one.
     */
    inline operator fun <reified T : ViewModel> get(key: String): T = get(key, T::class)

    /**
     * Returns an existing [ViewModel] with the given [key], or creates a new one.
     */
    operator fun <T : ViewModel> get(key: String, modelClass: KClass<T>): T =
        instance[key, modelClass.java]

    /**
     * Returns an existing [ViewModel] with the given [property] name, or creates a new one.
     */
    @MainThread
    inline operator fun <reified T : ViewModel> getValue(thisRef: Any?, property: KProperty<*>): T =
        get(property.name)
}
