package com.example.weatherappcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherappcompose.screens.DialogSearch
import com.example.weatherappcompose.screens.MainCard
import com.example.weatherappcompose.screens.TabLayout
import com.example.weatherappcompose.ui.theme.WeatherAppComposeTheme
import org.json.JSONObject

const val API_KEY = "da7349515c15420d922142133241808"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysList = remember {
                mutableStateOf(listOf<WeatherModel>())
            }
            val dialogState = remember {
                mutableStateOf(false)
            }

            val currentDay = remember {
                mutableStateOf(WeatherModel(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                ))
            }
            if (dialogState.value){
                DialogSearch(dialogState, onSubmit = {
                    getData(it,this,daysList,currentDay)

                })
            }
            getData("London",this,daysList,currentDay)
            WeatherAppComposeTheme {
                Image(
                    painterResource(id = R.drawable.weather_bg),
                    contentDescription = "im1",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
                Column {

                    MainCard(currentDay,
                        onClickSync = {
                        getData("London",this@MainActivity,daysList,currentDay)
                    },
                        onClickSearch = {
                        dialogState.value = true
                        }
                    )
                    TabLayout(daysList,currentDay)
                }

            }
        }
    }
}

private fun getData(city:String,context: Context, daysList: MutableState<List<WeatherModel>>,currentDay : MutableState<WeatherModel>){
        val url = "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY" +
                "&q=$city" +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

    val queue = Volley.newRequestQueue(context)

    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {responce->
            val list = getWeatherByDays(responce)
            currentDay.value=list[0]
            daysList.value=list
        },
        {
            Log.d("MyLog","Volley Error : $it")
        }
    )
    queue.add(sRequest)
}


private fun getWeatherByDays(response: String):List<WeatherModel> {
    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")

    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
    for (i in 0 until days.length()){
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition").getString("text"),
                item.getJSONObject("day").getJSONObject("condition").getString("icon"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONArray("hour").toString()
                )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}

