package com.example.linkedlearning.views.dashboard

import android.content.Context
import android.text.style.ClickableSpan
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.linkedlearning.Utils.Routes
import com.example.linkedlearning.components.*
import com.example.linkedlearning.data.api.course.data.Category
import com.example.linkedlearning.data.api.course.data.Course
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DashboardScreenViewModelFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = DashBoardViewModel(context) as T
}
@Composable
fun DashboardScreen(
    onNavigate : (to:String)->Unit,
    context: Context
){
    val viewModel:DashBoardViewModel = viewModel(factory = DashboardScreenViewModelFactory(context))
    runBlocking {}
    val coroutineScope = rememberCoroutineScope()
    val courses by viewModel.coursesList.observeAsState(List<Course?>(10){null})
    val categories = viewModel.categoryList.observeAsState(List<Category?>(10){null}).value
    LaunchedEffect(key1 = true){
        viewModel.getAllCourses(); viewModel.getCategories()
    }
    Scaffold(
        scaffoldState = rememberScaffoldState(),
        bottomBar = {
            BottomNavigation (backgroundColor = MaterialTheme.colors.secondary , modifier = Modifier.border(1.dp , MaterialTheme.colors.secondary)){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)

                ) {
                    Navbar(onNavigate = {
                        onNavigate(it) } ,  context = context)
                }
            }
        },
    ) {

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Log.i("UIEvent" , courses.size.toString())

            if(courses.isNotEmpty() && (courses[0] == null || categories[0] == null)){
               LoadingScreen()
            }else{

                Row(horizontalArrangement = Arrangement.Center){
                    SearchBar(
                        enteredText = {
                            coroutineScope.launch { viewModel.searchCourses(it); }
                        }
                    )
                }

                BannerAd(context)

                // Search by category
                Text("Search by Category" , modifier = Modifier.padding(10.dp), style = TextStyle(
                    fontSize = 30.sp
                ))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    item{
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    items(categories.size) { index ->
                        Spacer(modifier = Modifier.width(6.dp))
                        ClickableText(text = AnnotatedString(text = categories[index]!!.title) ,modifier = Modifier
                            .background(
                                Color.LightGray, shape = RoundedCornerShape(
                                    CornerSize(5.dp)
                                )
                            )
                            .padding(start = 5.dp, end = 5.dp), onClick = {
                            coroutineScope.launch {
                                viewModel.getCoursesByCategory(categories[index]!!._id)
                            }
                        })
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
                Spacer(modifier = Modifier.height(15.dp))
                Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(10.dp))
                Text("Currently Learning" , style = TextStyle(fontSize = 30.sp) , modifier = Modifier.padding(start = 10.dp))

                Spacer(modifier = Modifier.height(30.dp))

                Row(horizontalArrangement = Arrangement.Center , modifier = Modifier.fillMaxWidth()){
                    ClickableText(text = AnnotatedString(text = "View courses") , onClick = {
                        onNavigate(Routes.ENROLLEDCOURSES)
                    })
                }
                Spacer(modifier = Modifier.height(30.dp))

                Text("Explore courses" , style = TextStyle(fontSize = 30.sp) , modifier = Modifier.padding(start = 10.dp))
                Log.i("APIEvent" , courses.size.toString())
                if(courses.isNotEmpty()){
                    for(i in 0 until courses.size){
                        CourseCard(courses[i]!! , onCardClick = {
//                        loading = true;
                            runBlocking {
                                viewModel.setSelectedCourseId(courses[i]!!._id)
                            }
                            onNavigate(Routes.COURSEDETAILS)
                        })
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

            }
        }
    }
}

