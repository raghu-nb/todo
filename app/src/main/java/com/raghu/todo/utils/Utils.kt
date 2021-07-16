package com.raghu.todo.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun hideKeyBoard(activity:Activity){
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val currentFocusedItem: View? = activity.currentFocus
        currentFocusedItem?.let{
            inputMethodManager.hideSoftInputFromWindow(currentFocusedItem.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

fun<T> LiveData<T>.ObserveOnce(lifecycleOwner: LifecycleOwner,observer: Observer<T>){

    observe(lifecycleOwner,object:Observer<T>{
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })

}
