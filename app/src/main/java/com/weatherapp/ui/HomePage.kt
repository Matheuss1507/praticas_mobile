package com.weatherapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.Forecast
import com.weatherapp.model.MainViewModel
import com.weatherapp.model.Weather
import java.text.DecimalFormat

@Composable
fun HomePage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val citiesMap by viewModel.cities.collectAsStateWithLifecycle()
    val weatherMap by viewModel.weather.collectAsStateWithLifecycle(emptyMap())
    val forecastMap by viewModel.forecast.collectAsStateWithLifecycle(emptyMap())

    val selectedCityName = viewModel.city

    LaunchedEffect(selectedCityName) {
        selectedCityName?.let { name ->
            viewModel.loadWeather(name)
            viewModel.loadForecast(name)
        }
    }

    Column {
        if (selectedCityName == null) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Blue)
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp
                )
            }
        } else {
            val city = citiesMap[selectedCityName]
            val weather = weatherMap[selectedCityName] ?: Weather.LOADING

            Row {
                AsyncImage(
                    model = weather.imgUrl,
                    modifier = modifier.size(140.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )
                Column {
                    Spacer(modifier = modifier.size(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCityName,
                            fontSize = 28.sp
                        )
                        if (city != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            val icon = if (city.isMonitored) {
                                Icons.Filled.Notifications
                            } else {
                                Icons.Outlined.Notifications
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = "Monitorada?",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        viewModel.update(city = city.copy(isMonitored = !city.isMonitored))
                                    }
                            )
                        }
                    }
                    Spacer(modifier = modifier.size(12.dp))
                    Text(
                        text = weather.desc,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = modifier.size(12.dp))
                    Text(
                        text = "Temp: ${weather.temp} ℃",
                        fontSize = 22.sp
                    )
                }
            }

            forecastMap[selectedCityName]?.let { forecasts ->
                LazyColumn {
                    items(items = forecasts) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    forecast: Forecast,
    modifier: Modifier = Modifier,
    onClick: (Forecast) -> Unit
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = forecast.imgUrl,
            modifier = modifier.size(70.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = modifier.size(16.dp))
        Column {
            Text(modifier = modifier, text = forecast.weather, fontSize = 24.sp)
            Row {
                Text(modifier = modifier, text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = modifier.size(12.dp))
                Text(modifier = modifier, text = "Min: $tempMin ℃", fontSize = 16.sp)
                Spacer(modifier = modifier.size(12.dp))
                Text(modifier = modifier, text = "Max: $tempMax ℃", fontSize = 16.sp)
            }
        }
    }
}