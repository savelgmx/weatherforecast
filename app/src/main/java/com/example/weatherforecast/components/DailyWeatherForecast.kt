package com.example.weatherforecast.components


import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherforecast.domain.models.DailyWeather
import com.example.weatherforecast.domain.models.HourlyWeather
import com.example.weatherforecast.theme.Blue500
import com.example.weatherforecast.utils.WeatherUtils

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DailyWeatherForecast(
    navController: NavController,
    dailyList: List<DailyWeather>,
    hourlyList: List<HourlyWeather>,
    startIndex: Int = 0,
    timeZone: String
) {
    val pagerState = rememberPagerState(pageCount = { dailyList.size }, initialPage = startIndex)
    val localContext = LocalContext.current

    Scaffold(
        topBar = {
            val currentPage = pagerState.currentPage
            val currentDaily = dailyList.getOrNull(currentPage) ?: dailyList.firstOrNull() ?: return@Scaffold
            TopAppBar(
                title = { Text(text = WeatherUtils.updateDateToToday(currentDaily.dt.toInt())) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        HorizontalPager(state = pagerState, modifier = Modifier.padding(padding)) { page ->
            val daily = dailyList[page]
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Blue500)
            ) {
                val isLandscape = maxWidth > maxHeight
                if (isLandscape) {
                    // ——— LANDSCAPE: двухпанельный режим (split-pane) ———
                    // Левая панель (weight 0.5f): DailyWeatherCard + почасовой прогноз
                    // Правая панель (weight 0.5f): детальные карточки
                    Row(modifier = Modifier.fillMaxSize()) {
                        // ——— Left pane: DailyWeatherCard + hourly forecast ———
                        LazyColumn(
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxHeight()
                        ) {
                            dailyWeatherLeftItems(
                                daily = daily,
                                hourlyList = hourlyList,
                                timeZone = timeZone,
                                context = localContext
                            )
                        }

                        // ——— Vertical divider ———
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )

                        // ——— Right pane: detail cards ———
                        LazyColumn(
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxHeight()
                        ) {
                            dailyWeatherRightItems(
                                daily = daily,
                                timeZone = timeZone
                            )
                        }
                    }
                } else {
                    // ——— PORTRAIT: однопанельный режим (оригинальный скролл) ———
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        dailyWeatherLeftItems(
                            daily = daily,
                            hourlyList = hourlyList,
                            timeZone = timeZone,
                            context = localContext
                        )
                        dailyWeatherRightItems(
                            daily = daily,
                            timeZone = timeZone
                        )
                    }
                }
            }
        }
    }
}
