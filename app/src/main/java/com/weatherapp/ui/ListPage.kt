package com.weatherapp.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.City
import com.weatherapp.model.MainViewModel
import com.weatherapp.model.Weather
import com.weatherapp.ui.nav.Route

@Composable
fun ListPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val activity = LocalActivity.current as Activity

    val cityMap = viewModel.cities.collectAsStateWithLifecycle(emptyMap()).value
    val cityList = cityMap.values.toList().sortedBy { it.name }
    val weatherMap = viewModel.weather.collectAsStateWithLifecycle(emptyMap()).value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items = cityList, key = { it.name }) { city ->
            LaunchedEffect(city.name) {
                viewModel.loadWeather(city.name)
            }

            val weather = weatherMap[city.name] ?: Weather.LOADING

            CityItem(
                city = city,
                weather = weather,
                onClose = {
                    viewModel.remove(city)
                    Toast.makeText(activity, "Cidade excluída", Toast.LENGTH_LONG).show()
                },
                onClick = {
                    viewModel.city = city.name
                    viewModel.page = Route.Home
                    Toast.makeText(activity, "Cidade carregada", Toast.LENGTH_LONG).show()
                },
                onToggleMonitor = {
                    viewModel.update(city = city.copy(isMonitored = !city.isMonitored))
                }
            )
        }
    }
}

@Composable
fun CityItem(
    city: City,
    weather: Weather,
    onClick: () -> Unit,
    onClose: () -> Unit,
    onToggleMonitor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = if (weather == Weather.LOADING) "Carregando clima..." else weather.desc
    val icon = if (city.isMonitored) {
        Icons.Filled.Notifications
    } else {
        Icons.Outlined.Notifications
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = weather.imgUrl,
            modifier = modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = city.name,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = "Monitorada?",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onToggleMonitor() }
                )
            }
            Text(
                modifier = Modifier,
                text = desc,
                fontSize = 16.sp
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}