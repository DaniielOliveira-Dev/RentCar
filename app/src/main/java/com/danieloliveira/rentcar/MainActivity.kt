package com.danieloliveira.rentcar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.danieloliveira.rentcar.navigation.RentCarApp
import com.danieloliveira.rentcar.ui.theme.RentCarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RentCarTheme {
                RentCarApp(application = application as RentCarApplication)
            }
        }
    }
}
