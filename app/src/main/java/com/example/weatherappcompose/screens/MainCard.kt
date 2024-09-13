package com.example.weatherappcompose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherappcompose.R
import com.example.weatherappcompose.WeatherModel
import com.example.weatherappcompose.ui.theme.BlueMain
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(currentDay : MutableState<WeatherModel>,onClickSync :() -> Unit,onClickSearch :() -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueMain)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                                text = currentDay.value.time,
                                style = TextStyle(fontSize = 15.sp),
                                color = Color.White
                            )
                            AsyncImage(
                                model = "https:${currentDay.value.icon}",
                                contentDescription = "im2",
                                modifier = Modifier
                                    .size(35.dp)
                                    .padding(top = 3.dp, end = 6.dp)
                            )
                        }
                    }
                    Text(
                        text = currentDay.value.city,
                        style = TextStyle(fontSize = 24.sp),
                        color = Color.White
                    )
                    Text(
                        text = if(currentDay.value.currentTemp.isNotEmpty())
                            "${currentDay.value.currentTemp}°C"

                        else "${currentDay.value.maxTemp}°C/${currentDay.value.minTemp}°C",

                        style = TextStyle(fontSize = 60.sp),
                        color = Color.White
                    )
                    Text(
                        text = currentDay.value.condition,
                        style = TextStyle(fontSize = 16.sp),
                        color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            onClickSearch.invoke()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "im3",
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "${currentDay.value.maxTemp}°С/${currentDay.value.minTemp}°С",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.White
                        )

                        IconButton(onClick = {
                            onClickSync.invoke()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_sync_24),
                                contentDescription = "im4",
                                tint = Color.White
                            )
                        }

                    }
                }
            }
        }


    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList : MutableState<List<WeatherModel>>,currentDay: MutableState<WeatherModel>) {
    val tablist = listOf("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = { pos ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, pos)
                )
            },
            contentColor = Color.White, backgroundColor = BlueMain


        ) {
            tablist.forEachIndexed { index, text ->
                Tab(selected = false,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            text = text,
                            style = TextStyle(Color.White)
                        )
                    }
                )

            }
        }
        HorizontalPager(
            state = pagerState,
            count = tablist.size,
            modifier = Modifier.weight(1.0f)

        ) { index ->
            val list = when(index){
                0 -> getWeatherByHours(currentDay.value.hours)
                1 -> daysList.value
                else -> daysList.value

            }
            MainList(list,currentDay)

            }

        }
    }

private fun getWeatherByHours(hours:String) : List<WeatherModel>{

    if (hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<WeatherModel>()
    for (i in 0 until hoursArray.length()){
        val item = hoursArray[i] as JSONObject
        list.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c") + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
                )
        )
    }
    return list
}