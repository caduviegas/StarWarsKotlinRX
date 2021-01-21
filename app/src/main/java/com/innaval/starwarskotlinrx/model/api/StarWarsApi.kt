package com.innaval.starwarskotlinrx.model.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable

class StarWarsApi {
    val service: StarWarsApiDef
    init{

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://swapi.dev/api/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory((GsonConverterFactory.create(gson)))
            .client(httpClient.build())
            .build()

        service = retrofit.create<StarWarsApiDef>(StarWarsApiDef::class.java)
    }
    fun loadMovies() : Observable<Movie> {
        return service.listMovies()
                .flatMap { filmeResult -> Observable.from(filmeResult.results) }
                .flatMap { film -> Observable.just(Movie(
                        film.title,
                        film.episodeId,
                        ArrayList<Character>())
                ) }
    }
}