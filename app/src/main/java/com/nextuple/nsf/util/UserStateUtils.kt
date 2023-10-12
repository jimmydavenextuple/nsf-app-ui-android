package com.nextuple.nsf.util

import android.content.Context
import com.google.gson.Gson
import com.nextuple.nsf.service.dto.User
import com.nextuple.nsf.ui.state.UserViewModel

object UserStateUtils {
    fun saveUserState(userVM: UserViewModel, context: Context) {
        val userIsLoggedIn = userVM.user != null
        if (userIsLoggedIn) {
            val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val gson = Gson()
            editor.putBoolean("isUserLoggedIn", true)
            editor.putString("userInfo", gson.toJson(userVM.user).toString())
            editor.putString("errorMsg", userVM.errMsg)
            editor.putString("viewState", gson.toJson(userVM.viewState).toString())
            editor.apply()
        }
    }
    fun getUserState(userVM: UserViewModel, context: Context): UserViewModel {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false)
        return if (isUserLoggedIn) {
            val userInfo = sharedPreferences.getString("userInfo", "")
            val errorMsg = sharedPreferences.getString("errorMsg", "")
            val gson = Gson()
            val user = gson.fromJson(userInfo, User::class.java)
            UserViewModel(user = user, errMsg = errorMsg, userService = null, handler = null)
        } else {
            userVM
        }
    }

    fun clearUserState(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear() // Clear all data in the preferences
        editor.apply()
    }
}
