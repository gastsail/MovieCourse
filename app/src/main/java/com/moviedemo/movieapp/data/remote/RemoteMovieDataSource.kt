package com.moviedemo.movieapp.data.remote

import com.moviedemo.movieapp.data.model.MovieList
import com.moviedemo.movieapp.repository.WebService

class RemoteMovieDataSource(private val webService: WebService) {

    suspend fun getUpcomingMovies(): MovieList = webService.getUpcomingMovies(com.moviedemo.movieapp.application.AppConstants.API_KEY)

    suspend fun getTopRatedMovies(): MovieList = webService.getTopRatedMovies(com.moviedemo.movieapp.application.AppConstants.API_KEY)

    suspend fun getPopularMovies(): MovieList = webService.getPopularMovies(com.moviedemo.movieapp.application.AppConstants.API_KEY)

}