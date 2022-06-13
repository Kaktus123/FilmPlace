package com.example.filmplayer.fragment

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.filmfinder.dto.movie.details.MovieDetailsDTO
import com.example.filmplayer.MainActivity
import com.example.filmplayer.R
import com.example.filmplayer.databinding.FragmentHomeBinding
import com.example.filmplayer.dto.movie.search.MovieIdDTO
import com.example.filmplayer.dto.movie.search.PhraseDTO
import com.example.filmplayer.entity.UserInfo
import com.example.filmplayer.service.MDBListService
import com.example.filmplayer.service.UserService
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso


class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userService = UserService()
    private val mdbListService = MDBListService()

    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        private const val TAG = "HOME_FRAGMENT"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchMovieButtonBinding(view)
        removeSoftKeyboardOnViewClick()
        enableBlockingCallsToMDBList()
        displayFavouriteMovies()
        displayWatchlist()
    }

    private fun removeSoftKeyboardOnViewClick() {
        binding.root.setOnClickListener {
            (activity as MainActivity?)!!.hideSoftKeyboard(binding.root)
        }
    }

    private fun searchMovieButtonBinding(view: View) {
        binding.searchMovieButton.setOnClickListener {
            val phrase = binding.searchBar.text.toString()

            if (phrase.isNotEmpty()) {
                val phraseDTO = PhraseDTO(phrase)
                val bundle = bundleOf("phrase" to phraseDTO)
                view.findNavController().navigate(R.id.searchResultsFragment, bundle)
            } else {
                Toast.makeText(activity, resources.getString(R.string.searchBarHint), Toast.LENGTH_SHORT).show()
            }

            (activity as MainActivity?)!!.hideSoftKeyboard(binding.root)
        }
    }

    private fun enableBlockingCallsToMDBList() {
        val SDK_INT: Int = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    private fun displayFavouriteMovies() {
        userService.getUserInfo().addOnSuccessListener {
            val value = it.getValue(UserInfo::class.java)
            if (value?.favouriteMoviesId == null) {
                binding.favouriteMoviesContainer.removeAllViews()
                val textView = TextView(requireContext())
                textView.text = resources.getString(R.string.noFavouriteMovies)
                binding.favouriteMoviesContainer.addView(textView)
            } else {
                val movieDetailsList = mdbListService.getMovieDetailsList(value.favouriteMoviesId!!)
                populateContainer(movieDetailsList, binding.favouriteMoviesContainer)
            }
        }
    }

    private fun displayWatchlist() {
        userService.getUserInfo().addOnSuccessListener {
            val value = it.getValue(UserInfo::class.java)
            if (value?.watchlistMoviesId == null) {
                binding.watchlistContainer.removeAllViews()
                val textView = TextView(requireContext())
                textView.text = resources.getString(R.string.noWatchlist)
                binding.watchlistContainer.addView(textView)
            } else {
                val movieDetailsList = mdbListService.getMovieDetailsList(value.watchlistMoviesId!!)
                populateContainer(movieDetailsList, binding.watchlistContainer)
            }
        }
    }

    private fun populateContainer(movieDetailsList: List<MovieDetailsDTO>, container:LinearLayout) {
        container.removeAllViews()
        for (movieDetailsDTO in movieDetailsList) {
            val button = ImageView(requireContext())
            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            button.layoutParams.height =
                requireContext().resources.getDimensionPixelSize(R.dimen.posterHeight)
            button.adjustViewBounds = true
            button.scaleType = ImageView.ScaleType.FIT_CENTER
            Picasso.get().load(movieDetailsDTO.poster).into(button);

            button.setOnClickListener {
                val movieIdDTO = MovieIdDTO(movieDetailsDTO.imdbid)
                val bundle = bundleOf("movieId" to movieIdDTO)
                requireView().findNavController().navigate(R.id.movieDetailsFragment, bundle)
            }
            container.addView(button)
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //user not logged in
            Log.d(TAG, "checkUser: user not logged in!")
        } else {
            setUsernameAsNavHeaderText(firebaseUser)
        }
    }

    private fun setUsernameAsNavHeaderText(firebaseUser: FirebaseUser) {
        val navigationView: NavigationView = requireActivity().findViewById(R.id.navView)
        val header: View = navigationView.getHeaderView(0)
        val text: TextView = header.findViewById(R.id.welcomeTv)
        text.text = "Welcome ${firebaseUser.displayName}!"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}