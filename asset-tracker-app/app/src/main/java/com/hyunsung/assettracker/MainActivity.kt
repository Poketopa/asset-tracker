package com.hyunsung.assettracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.hyunsung.assettracker.ui.theme.AssetTrackerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AssetTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as ComponentActivity
    val messageState = remember { mutableStateOf("서버 응답을 기다립니다...") }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = messageState.value,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            activity.lifecycleScope.launch {
                try {
                    val response = RetrofitClient.testApi.getTestMessage()
                    if (response.isSuccessful) {
                        val message = response.body()?.message ?: "응답 없음"
                        messageState.value = "서버 응답: $message"
                    } else {
                        messageState.value = "서버 오류: ${response.code()}"
                    }
                } catch (e: Exception) {
                    messageState.value = "통신 실패: ${e.message}"
                }
            }
        }) {
            Text("새로고침")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AssetTrackerTheme {
        Greeting("Android")
    }
}