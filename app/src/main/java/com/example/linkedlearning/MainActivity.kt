package com.example.linkedlearning


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.linkedlearning.Utils.Routes
import com.example.linkedlearning.ui.theme.LinkedLearningTheme
import com.example.linkedlearning.views.User.ProfileView.ProfileScreen
import com.example.linkedlearning.views.auth.OTPverify.OTPScreen
import com.example.linkedlearning.views.dashboard.DashboardScreen
import com.example.linkedlearning.views.auth.login.LoginScreen
import com.example.linkedlearning.views.auth.signup.SignupScreen
import com.example.linkedlearning.views.courseShow.EnrolledCourses.EnrolledCoursesScreen
import com.example.linkedlearning.views.courseShow.NewQuestion.NewQuestionScreen
import com.example.linkedlearning.views.courseShow.QuestionShow.QuestionShowScreen
import com.example.linkedlearning.views.courseShow.courseDetails.CourseDetailsScreen
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var instance:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("UIEvent" , "before init")
        MobileAds.initialize(this) {}
        Log.i("UIEvent" , "passed init")
        val context: Context = this
        setContent {
            LinkedLearningTheme {
                val navController = rememberNavController();
                val scaffoldState = rememberScaffoldState()
                Scaffold(scaffoldState = scaffoldState){
                    NavHost(navController = navController, startDestination = Routes.LOGIN){
                        composable(Routes.LOGIN){
                            LoginScreen (
                                onNavigate = {
                                    navController.popBackStack()
                                    navController.navigate(it)
                                },
                                context = context,
                                showSnackBar = {
                                    GlobalScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                        composable(Routes.DASHBOARD){
                            DashboardScreen(
                                onNavigate = {
                                    navController.navigate(it)},
                                context = context
                            )
                        }
                        composable(Routes.SIGNUP){
                            SignupScreen(
                                onNavigate = {
                                    navController.popBackStack()
                                    navController.navigate(it)
                                },
                                context = context,
                                showSnackBar = {
                                    GlobalScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                        composable(Routes.OTPVERIFY){
                            OTPScreen(
                                onNavigate ={
                                    navController.popBackStack()
                                    navController.navigate(it)
                                },
                                context = context,
                                showSnackBar = {
                                    GlobalScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                        composable(Routes.COURSEDETAILS){
                            CourseDetailsScreen(onNavigate = {
                                navController.navigate(it)
                            }, context = context ,
                                showSnackBar = {
                                    GlobalScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                        composable(Routes.ENROLLEDCOURSES){
                            EnrolledCoursesScreen(onNavigate = {
                                navController.navigate(it)
                            }, context = context
                            )
                        }
                        composable(Routes.NEWQUESTION){
                            NewQuestionScreen(onNavigate = {
                                navController.navigate(it)
                            }, context = context,showSnackBar = {
                                GlobalScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(it)
                                }
                            }
                            )
                        }
                        composable(Routes.SHOWQUESTION){
                            QuestionShowScreen(onNavigate = {
                                navController.navigate(it)
                            }, context = context,showSnackBar = {
                                GlobalScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(it)
                                }
                            }
                            )
                        }
                        composable(Routes.USERPROFILE){
                            ProfileScreen(onNavigate = {
                                navController.navigate(it)
                            }, context = context,
                                showSnackBar = {
                                    GlobalScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    fun getInstance():MainActivity{
        return instance
    }
}
