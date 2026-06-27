package com.weatherapp.ui

import android.app.Activity

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.model.City
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.weatherapp.R
import com.weatherapp.model.MainViewModel
import com.weatherapp.model.Weather
import com.weatherapp.ui.nav.Route

@Composable
fun ListPage(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val cityList = viewModel.cities
    val activity = LocalActivity.current as Activity // Para os Toasts
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items = cityList, key = { it.name } ) { city ->
            CityItem(city = city, weather = viewModel.weather(city.name), onClose = {
                viewModel.remove(city)
                Toast.makeText(activity, "Cidade excluída", Toast.LENGTH_LONG).show()
            }, onClick = {
                viewModel.city = city.name
                viewModel.page = Route.Home
                Toast.makeText(activity, "Cidade carregada", Toast.LENGTH_LONG).show()
            })
        }
    }
}
@Composable
fun CityItem(
    city: City,
    weather: Weather,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = if (weather == Weather.LOADING) "Carregando clima..." else weather.desc
    Row(
        modifier = modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage( // Substitui o Icon(...)
            model = weather.imgUrl,
            modifier = modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(modifier = modifier.weight(1f)) {
            Text(modifier = Modifier,
                text = city.name,
                fontSize = 24.sp)
            Text(modifier = Modifier,
                text = desc,
                fontSize = 16.sp)
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}