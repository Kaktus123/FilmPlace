package com.example.filmplayer.fragment

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
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
                for (i in 0 until movieDetails.streams.size) {
                    val streamTextView = TextView(requireContext())
                    streamTextView.text = movieDetails.streams[i].name
//                    binding.root.addView(streamTextView)
                    streamTextView.textSize = 18.0f
                    streamTextView.setPadding(40, 0,40,0)
                    streamTextView.setTypeface(Typeface.DEFAULT_BOLD)
                    streamTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryDarkColor))
                    binding.streamsLinearLayout.addView(streamTextView)

                    if (i != movieDetails.streams.size-1) {
                        val streamTextView = TextView(requireContext())
                        streamTextView.text = "|"
//                    binding.root.addView(streamTextView)
                        streamTextView.textSize = 20.0f
                        streamTextView.setTypeface(Typeface.DEFAULT_BOLD)
                        streamTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryColor))
                        binding.streamsLinearLayout.addView(streamTextView)
                    }
                }


//                for (streamDTO in movieDetails.streams) {
//                    val streamTextView = TextView(requireContext())
//                    streamTextView.text = streamDTO.name
////                    binding.root.addView(streamTextView)
//                    streamTextView.textSize = 20.0f
//                    streamTextView.setTypeface(Typeface.DEFAULT_BOLD)
//                    streamTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryDarkColor))
//                    binding.streamsLinearLayout.addView(streamTextView)
//
////                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
////                        LinearLayout.LayoutParams.WRAP_CONTENT,
////                        LinearLayout.LayoutParams.WRAP_CONTENT
////                    )
////                    params.setMargins(10, 20, 10, 10)
////
////                    streamTextView.setLayoutParams(params)
//
//
//                }
            }
        }
    }
}