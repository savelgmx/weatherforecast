package com.example.weatherforecast.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherforecast.R
import com.example.weatherforecast.presentation.ui.theme.orange
import com.example.weatherforecast.presentation.ui.theme.white
import com.example.weatherforecast.theme.Blue800
import com.example.weatherforecast.theme.QuickSandTypography
import com.example.weatherforecast.utils.WeatherUtils

@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE1F5FE))
            .padding(16.dp)
    ) {
        Text("Погода сейчас", fontWeight = FontWeight.Bold, fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WindSpeedCard(speed = 11, windDegree = 135)
            HumidityCard(humidity = 35, dewPoint = 12)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            UVIndexCard(index = 1)
            PressureCard(pressure = 1010)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MoonriseMoonsetCard( moonPhase = 0.43)
            SunriseSunsetCard(sunrise = "04:04", sunset = "21:39", dawn = "03:02", dusk = "22:41")
        }
    }
}

@Composable
fun HumidityCard(humidity: Int, dewPoint: Int) {
    val context= LocalContext.current
    val switchState    by DataStoreManager.tempSwitchPrefFlow(context).collectAsState(initial = false)
    val dewPointValue= WeatherUtils.updateTemperature(dewPoint,switchState)
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                context.getString(R.string.humidity),
                fontWeight = FontWeight.Medium,
                color = Color.White,
                style= QuickSandTypography.titleMedium
            )
            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800)
                ,
                initialValue = humidity,
                primaryColor = orange,
                secondaryColor = white,
                circleRadius = 80f, valueName = "%"
            )

            Text("${context.getString(R.string.dew_point)} $dewPointValue",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = QuickSandTypography.bodyMedium
            )
        }
    }
}

@Composable
fun UVIndexCard(index: Int) {
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(LocalContext.current.getString(R.string.uv_index), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)

            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800)
                ,

                initialValue = index,
                primaryColor = orange,
                secondaryColor = white ,
                circleRadius = 80f, minValue = 0, maxValue = 13
            )

            Text(WeatherUtils.updateUVLevel(LocalContext.current,index),
                color = Color.White, style = QuickSandTypography.headlineSmall)
        }
    }
}

@Composable
fun PressureCard(pressure: Int) {
    val context= LocalContext.current
    val pressureValue= WeatherUtils.updatePressure(pressureValue = pressure)
    val pressureUnit= WeatherUtils.updatePressureUnit()

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(context.getString(R.string.pressure), fontSize = 16.sp, fontWeight = FontWeight.Medium,color=Color.White)
            CustomCircularProgressIndicator(
                modifier = Modifier
                    .size(85.dp)
                    .background(Blue800)
                ,
                initialValue = pressureValue,
                primaryColor = orange,
                secondaryColor = white,
                circleRadius = 80f,
                minValue = WeatherUtils.updateMinMaxPressureValue(minMaxPressure = 870),
                maxValue = WeatherUtils.updateMinMaxPressureValue(minMaxPressure = 1033)
            )
            Text("$pressureValue $pressureUnit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)

        }
    }
}

@Composable
fun SunriseSunsetCard(sunrise: String, sunset: String, dawn: String, dusk: String) {
    val context= LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(all=10.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {

            Row    (modifier = Modifier.padding(all=1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)         {

                Text(context.getString(R.string.sunrise)+": ",
                    color =  Color.White, style = QuickSandTypography.titleMedium, fontWeight = FontWeight.Bold)
                Text(sunrise, fontWeight = FontWeight.Bold, color =  Color.White, style = QuickSandTypography.bodyMedium)

            }

 /*           Row (modifier = Modifier.padding(all=1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {

                Text(sunrise, fontWeight = FontWeight.Bold, color =  Color.White, style = QuickSandTypography.bodyMedium)
            }
*/
            Row    (modifier = Modifier.padding(all=10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)         {
                //Dawn
                Text(context.getString(R.string.dawn)+": ",
                    color =  Color.White, style = QuickSandTypography.titleMedium, fontWeight = FontWeight.Bold)
                Text(dawn, fontWeight = FontWeight.Bold, color =  Color.White, style = QuickSandTypography.titleMedium)
            }




            Row(modifier = Modifier.padding(all=1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)  {
                Text(context.getString(R.string.sunset)+": ",
                    color = Color.White,style = QuickSandTypography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(sunset, fontWeight = FontWeight.Bold, color = Color.White, style = QuickSandTypography.titleMedium)

            }




            Row(modifier = Modifier.padding(all=1.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center)  {
                Text(context.getString(R.string.dusk)+": ",
                    color = Color.White,style = QuickSandTypography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(dusk, fontWeight = FontWeight.Bold, color = Color.White, style = QuickSandTypography.bodyMedium)

            }


        } //Column 1


    }//surface
}

@Composable
fun MoonriseMoonsetCard(
    moonPhase:Double

){
    val localContext= LocalContext.current
    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = (Blue800),
        elevation = 8.dp)

    {
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Row(modifier = Modifier.padding(1.dp)) {
                val moonPhaseIconId = WeatherUtils.getMoonPhaseIconName(localContext, moonPhase)
                AsyncImage(
                    model = moonPhaseIconId,
                    contentDescription = "MoonPhase icon",
                    modifier = Modifier
                        .size(64.dp) // Define your desired width and height
                        .padding(all = 1.dp)
                )
          }
            Row(modifier = Modifier.padding(1.dp)){
                androidx.compose.material3.Text(
                    text = WeatherUtils.calculateMoonPhase(localContext, moonPhase),
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    style = QuickSandTypography.titleMedium,
                    modifier = Modifier.padding(all = 10.dp)
                )

            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WeatherScreen()
}
