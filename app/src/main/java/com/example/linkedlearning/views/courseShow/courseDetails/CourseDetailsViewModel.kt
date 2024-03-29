package com.example.linkedlearning.views.courseShow.courseDetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.linkedlearning.data.api.ApiCore
import com.example.linkedlearning.data.api.auth.data.LoginRes
import com.example.linkedlearning.data.api.course.CourseAPI
import com.example.linkedlearning.data.api.course.data.Course
import com.example.linkedlearning.data.api.course.data.DiscussionQuestions
import com.example.linkedlearning.data.api.course.data.Question
import com.example.linkedlearning.data.courseData.CourseRepo
import com.example.linkedlearning.views.UIevents
import com.google.gson.Gson
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CourseDetailsViewModel(private val context : Context):ViewModel() {
    private val repoInstance = CourseRepo(context)

    private val _courseData = MutableLiveData<Course>()
    val courseData : LiveData<Course>
    get() = _courseData

    private val _currentView = MutableLiveData<String>("SYLLABUS")
    val currentView : LiveData<String>
    get() = _currentView

    private val _questions = MutableLiveData<List<Question>>()
    val questions : LiveData<List<Question>>
    get() = _questions

    private val retrofitInstance = ApiCore(this.context).getInstance().create(CourseAPI::class.java)

    private val eventChannel = Channel<UIevents>()

    // Receiving channel as a flow
    val eventFlow = eventChannel.receiveAsFlow()


    fun triggerEvents(event: UIevents) = viewModelScope.launch {
        eventChannel.send(event)
    }

    suspend fun getCourseData():Boolean{
        val courseId = repoInstance.getSelectedCourseId()
        Log.i("APIEvent" , courseId.toString())
        val response = try{
            retrofitInstance.getCourseById(courseId!!)
        }catch(e:IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Please check your internet connection"))
            return false
        }catch(e: HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar(msg = "Something went wrong. Please try again later"))
            return false
        }
        Log.i("APIEvent" , response.code().toString())

        if(response.code() == 200 && response.body() != null){
            this._courseData.value = response.body()!!.foundCourse
        }else if(response.errorBody() != null){
            val errResponse = Gson().fromJson(response.errorBody()!!.string() , LoginRes::class.java)
            triggerEvents(UIevents.ShowErrorSnackBar(errResponse.err))
            return false
        }
        return false
    }

    fun setCurrentViewModel(to:String):Unit{
        this._currentView.value = to
    }

    suspend fun enrollIntoCourse(_id:String):Boolean{
        val response = try{
            retrofitInstance.enrollIntoCourse(_id)
        }catch(e:IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Please check your internet connection"))
            return false
        }catch(e:HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar(msg = "Something went wrong. Please try again later"))
            return false
        }
        if(response.code() == 200 && response.body() != null){
            Log.i("APIEvent" , "Enrolled")
            return true
        }else if(response.errorBody() != null){
            Log.i("APIEvent" , "Not enrolled")
            val errResponse = Gson().fromJson(response.errorBody()!!.string() , LoginRes::class.java)
            triggerEvents(UIevents.ShowErrorSnackBar(errResponse.err))
            return false
        }
        return false

    }

    suspend fun getQuestions():Boolean{

        val courseId = repoInstance.getSelectedCourseId()
        val response = try{
            retrofitInstance.getAllQuestions(courseId!!)
            //Log.i("APIEvent" , "after sending")
        }catch(e:IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Please check your internet connection"))
            return false
        }catch(e:HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar(msg = "Something went wrong. Please try again later"))
            return false
        }
        if(response.code() == 200 && response.body() != null){
            this._questions.value = response.body()!!.questions
            return true
        }
        return false
    }

    suspend fun setLectureId(_id: String):Unit{
        repoInstance.setSelectedLectureId(_id)
    }

    suspend fun rateCourse(rate:Int):Boolean{
        val courseId = repoInstance.getSelectedCourseId()
        val response = try{
            Log.i("APIEvent" , "Before sending")
            retrofitInstance.rateCourse(courseId!! , rate)
        }catch(e:IOException){
            triggerEvents(UIevents.ShowErrorSnackBar("Please check your internet connection"))
            return false
        }catch(e:HttpException){
            triggerEvents(UIevents.ShowErrorSnackBar(msg = "Something went wrong. Please try again later"))
            return false
        }
        if(response.code() == 200 && response.body() != null){
            return true
        }
        return false
    }
}