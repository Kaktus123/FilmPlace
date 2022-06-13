package com.example.filmplayer.fragment

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.filmfinder.dto.movie.details.MovieDetailsDTO
import com.example.filmplayer.R
import com.example.filmplayer.databinding.FragmentMovieDetailsBinding
import com.example.filmplayer.dto.movie.search.MovieIdDTO
import com.example.filmplayer.entity.UserInfo
import com.example.filmplayer.service.MDBListService
import com.example.filmplayer.service.UserService
import com.squareup.picasso.Picasso

class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {
    private var _binding: FragmentMovieDetailsBinding? = null;
    private val binding get() = _binding!!
    private val userService = UserService()
    private val mdbListService = MDBListService()

    private lateinit var movieId: MovieIdDTO

    private companion object {
        private const val TAG = "MOVIE_DETAILS_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            movieId = bundle.getParcelable("movieId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mdbListService.getMovieDetails(movieId.id, this)

        binding.addToFavouritesBtn.setOnClickListener {
            userService.addMovieToFavourites(movieId.id)
            showRemoveFromFavouritesButton()
        }

        binding.addToWatchlistBtn.setOnClickListener {
            userService.addMovieToWatchlist(movieId.id)
            showRemoveFromWatchlistButton()
        }

        binding.removeFromFavouritesBtn.setOnClickListener {
            userService.removeMovieFromFavourites(movieId.id)
            showAddToFavouritesButton()
        }

        binding.removeFromWatchlistBtn.setOnClickListener {
            userService.removeMovieFromWatchlist(movieId.id)
            showAddToWatchlistButton()
        }

        userService.getUserInfo().addOnSuccessListener {
            val value = it.getValue(UserInfo::class.java)
            if (value?.favouriteMoviesId != null && value.favouriteMoviesId!!.contains(movieId.id)) {
                showRemoveFromFavouritesButton()
            } else {
                showAddToFavouritesButton()
            }

            if (value?.watchlistMoviesId != null && value.watchlistMoviesId!!.contains(movieId.id)) {
                showRemoveFromWatchlistButton()
            } else {
                showAddToWatchlistButton()
            }
        }
    }

    private fun showAddToFavouritesButton() {
        binding.addToFavouritesBtn.visibility = View.VISIBLE
        binding.removeFromFavouritesBtn.visibility = View.GONE
    }

    private fun showRemoveFromFavouritesButton() {
        binding.addToFavouritesBtn.visibility = View.GONE
        binding.removeFromFavouritesBtn.visibility = View.VISIBLE
    }

    private fun showAddToWatchlistButton() {
        binding.addToWatchlistBtn.visibility = View.VISIBLE
        binding.removeFromWatchlistBtn.visibility = View.GONE
    }

    private fun showRemoveFromWatchlistButton() {
        binding.addToWatchlistBtn.visibility = View.GONE
        binding.removeFromWatchlistBtn.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    fun updateView(movieDetails: MovieDetailsDTO) {
        requireActivity().runOnUiThread {
            Picasso.get().load(movieDetails.backdrop).into(binding.movieBackdrop);
            binding.movieDetailsTitle.text = movieDetails.title
            binding.movieDetailsDescription.text = movieDetails.description
            binding.movieDetailsReleaseDate.text = "Release date: ${movieDetails.released}"
            binding.movieDetailsScore.text = "Rating: ${movieDetails.score}/100"

            if (movieDetails.streams.isEmpty()) {
                binding.streamingPlatformsDescription.text = resources.getString(R.string.streamingPlatformsNotAvailable)
            } else {
                for (streamDTO in movieDetails.streams) {
                    val button = TextView(requireContext())
                    button.text = streamDTO.name
                    binding.root.addView(button)
                }
            }
        }
    }
}