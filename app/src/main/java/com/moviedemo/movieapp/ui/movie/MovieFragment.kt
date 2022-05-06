package com.moviedemo.movieapp.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.moviedemo.movieapp.databinding.FragmentMovieBinding
import com.moviedemo.movieapp.R
import com.moviedemo.movieapp.data.local.AppDatabase
import com.moviedemo.movieapp.data.local.LocalMovieDataSource
import com.moviedemo.movieapp.data.model.Movie
import com.moviedemo.movieapp.data.remote.RemoteMovieDataSource
import com.moviedemo.movieapp.presentation.MovieViewModel
import com.moviedemo.movieapp.presentation.MovieViewModelFactory
import com.moviedemo.movieapp.repository.MovieRepositoryImpl
import com.moviedemo.movieapp.repository.RetrofitClient
import com.moviedemo.movieapp.ui.movie.adapters.MovieAdapter
import com.moviedemo.movieapp.ui.movie.adapters.concat.PopularConcatAdapter
import com.moviedemo.movieapp.ui.movie.adapters.concat.TopRatedConcatAdapter
import com.moviedemo.movieapp.ui.movie.adapters.concat.UpcomingConcatAdapter

class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {

    private lateinit var binding: FragmentMovieBinding
    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepositoryImpl(
                RemoteMovieDataSource(RetrofitClient.webservice),
                LocalMovieDataSource(AppDatabase.getDatabase(requireContext()).movieDao())
            )
        )
    }
    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)

        concatAdapter = ConcatAdapter()

        viewModel.fetchMainScreenMovies().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is com.moviedemo.movieapp.core.Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is com.moviedemo.movieapp.core.Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    concatAdapter.apply {
                        addAdapter(
                            0,
                            UpcomingConcatAdapter(
                                MovieAdapter(
                                    result.data.first.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            1,
                            TopRatedConcatAdapter(
                                MovieAdapter(
                                    result.data.second.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            2,
                            PopularConcatAdapter(
                                MovieAdapter(
                                    result.data.third.results,
                                    this@MovieFragment
                                )
                            )
                        )
                    }

                    binding.rvMovies.adapter = concatAdapter
                }
                is com.moviedemo.movieapp.core.Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Error", "${result.exception}")
                }
            }
        })

    }

    override fun onMovieClick(movie: Movie) {
        val action = MovieFragmentDirections.actionMovieFragmentToMovieDetailFragment(
            movie.poster_path,
            movie.backdrop_path,
            movie.vote_average.toFloat(),
            movie.vote_count,
            movie.overview,
            movie.title,
            movie.original_language,
            movie.release_date
        )
        findNavController().navigate(action)
    }
}