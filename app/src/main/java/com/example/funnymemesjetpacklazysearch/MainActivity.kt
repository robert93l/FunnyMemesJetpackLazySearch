package com.example.funnymemesjetpacklazysearch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.funnymemesjetpacklazysearch.models.Meme
import com.example.funnymemesjetpacklazysearch.screens.DetailsScreen
import com.example.funnymemesjetpacklazysearch.screens.MainScreen
import com.example.funnymemesjetpacklazysearch.ui.theme.FunnyMemesJetpackLazySearchTheme
import com.example.funnymemesjetpacklazysearch.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FunnyMemesJetpackLazySearchTheme {

                val navController = rememberNavController()
                var memeList by remember {
                    mutableStateOf(listOf<Meme>())
                }
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = true) {
                    scope.launch(Dispatchers.IO) {
                        val response = try {
                            RetrofitInstance.api.getMemesList()
                        } catch (e: IOException) {

                            Toast.makeText(
                                this@MainActivity,
                                "app error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        } catch (e: retrofit2.HttpException) {
                            Toast.makeText(
                                this@MainActivity,
                                "HTTP error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null) {
                            withContext(Dispatchers.Main) {
                                memeList = response.body()!!.data.memes

                            }
                        }
                    }
                }



                NavHost(navController = navController, startDestination = "MainScreen") {

                    composable(route = "MainScreen") {
                        MainScreen(memesList = memeList, navController = navController)
                    }
                    composable(route = "DetailsScreen?name={name}&url={url}",
                        arguments = listOf(
                        navArgument(name = "name"){
                            type = NavType.StringType
                        },
                        navArgument(name = "url"){
                            type = NavType.StringType
                        }
                    )) {
                        DetailsScreen(
                            name = it.arguments?.getString("name"),
                            url = it.arguments?.getString("url")
                        )
                    }
                }

            }
        }
    }
}

