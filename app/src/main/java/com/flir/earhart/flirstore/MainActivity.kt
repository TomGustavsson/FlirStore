package com.flir.earhart.flirstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.flir.earhart.flirstore.composables.AppListScreen
import com.flir.earhart.flirstore.ui.theme.FlirStoreTheme
import com.flir.earhart.flirstore.viewmodel.FlirStoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel<FlirStoreViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlirStoreTheme {
                // A surface container using the 'background' color from the theme
                AppListScreen(viewModel)
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlirStoreTheme {
        Greeting("Android")
    }
}