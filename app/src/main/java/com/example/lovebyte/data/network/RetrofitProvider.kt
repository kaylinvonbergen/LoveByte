import com.example.lovebyte.data.network.WeatherApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitProvider {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val weatherApi: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApi::class.java)
    }
}