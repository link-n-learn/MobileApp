package com.example.linkedlearning.views.User.ProfileView

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkedlearning.data.api.ApiCore
import com.example.linkedlearning.data.api.course.CourseAPI
import com.example.linkedlearning.data.api.course.data.Course
import com.example.linkedlearning.data.api.user.UserAPI
import com.example.linkedlearning.data.courseData.CourseRepo
import com.example.linkedlearning.views.UIevents
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel(private val context: Context):ViewModel() {

    val username = MutableLiveData<String?>()
//    var username:String = "123"
    val email = MutableLiveData<String?>()
    val imageUrl = MutableLiveData<String?>()

//    var courses:List<Course> = listOf()

    val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>>
    get() = _courses


    private val eventChannel = Channel<UIevents>()

    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()
    fun triggerEvents(event: UIevents) = viewModelScope.launch{
        eventChannel.send(event)
    }

    private val repoInstance = CourseRepo(context)
    private val retrofitInstance = ApiCore(this.context).getInstance().create(UserAPI::class.java)
    private val coursesRetrofitInstance = ApiCore(this.context).getInstance().create(CourseAPI::class.java)

    suspend fun getUserData():Boolean{
        val response = try{
            retrofitInstance.getUserData()
        }catch(e: IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Check your internet connection and try again"))
            return false
        }catch(e: HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar("Something went wrong try again later"))
            return false
        }
        if(response.code() == 200 && response.body() != null){
            Log.i("APIEvent" , response.body().toString())
            username.value = response.body()!!.userData.username
            email.value = response.body()!!.userData.email
            imageUrl.value = response.body()!!.userData.image
            return true
        }
        return false
    }

    suspend fun getCreatedCourses():Boolean{
        val response = try {
            coursesRetrofitInstance.getUserCreatedCourses()
        }catch(e: IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Check your internet connection and try again"))
            return false
        }catch(e: HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar("Something went wrong try again later"))
            return false
        }

        if(response.code() == 200 && response.body() != null){
            this._courses.value = response.body()!!.courses
            return true
        }
        return false
    }

    suspend fun setSelectedCourseId(_id:String){
        repoInstance.setSelectedCourseId(_id)
    }
}