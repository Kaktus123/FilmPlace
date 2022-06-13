package com.example.filmplayer.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.filmfinder.dto.movie.search.MoviesDTO
import com.example.filmfinder.dto.movie.search.SearchDTO
import com.example.filmplayer.R
import com.example.filmplayer.databinding.FragmentSearchResultsBinding
import com.example.filmplayer.dto.movie.search.MovieIdDTO
import com.example.filmplayer.dto.movie.search.PhraseDTO
import com.example.filmplayer.service.MDBListService


class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    private var _binding: FragmentSearchResultsBinding? = null;
    private val binding get() = _binding!!
    private lateinit var phrase: PhraseDTO
    private val mdbListService = MDBListService()

    private companion object {
        private const val TAG = "SEARCH_RESULT_FRAGMENT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            phrase = bundle.getParcelable("phrase")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultsBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mdbListService.searchForMovie(phrase.phrase, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
    }

    fun updateView(movies: MoviesDTO) {
        if (movies.search.isEmpty()) {
            addNoMoviesFoundTextView()
        } else {
            populateListViewWithMoviesFound(movies)
        }
    }

    private fun addNoMoviesFoundTextView() {
        requireActivity().runOnUiThread {
            val textView = TextView(requireContext())
            textView.text = resources.getString(R.string.noMoviesFound)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            textView.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
            textView.typeface = Typeface.DEFAULT_BOLD;
            binding.searchResultsFragment.addView(textView)
        }
    }

    private fun populateListViewWithMoviesFound(movies: MoviesDTO) {
        val mListView = requireView().findViewById<ListView>(R.id.moviesList)

        mListView.setOnItemClickListener { parent, view, position, id ->
            val searchDTO: SearchDTO = parent.getItemAtPosition(position) as SearchDTO
            Log.d(TAG, "onResponse: Selected movie result with id ${searchDTO.id}")
            val movieIdDTO = MovieIdDTO(searchDTO.id)
            val bundle = bundleOf("movieId" to movieIdDTO)
            view.findNavController().navigate(R.id.movieDetailsFragment, bundle)
        }

        requireActivity().runOnUiThread {
            mListView.adapter = MoviesFoundAdapter(movies.search, requireActivity().applicationContext)
        }
    }

    class MoviesFoundAdapter(moviesFound: List<SearchDTO>, context: Context) : BaseAdapter() {

        private val mContext: Context
        private val mMoviesFound: List<SearchDTO>

        init {
            this.mContext = context
            this.mMoviesFound = moviesFound
        }

        override fun getCount(): Int {
            return mMoviesFound.size;
        }

        override fun getItem(position: Int): Any {
            return mMoviesFound.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.movie_found_row, parent, false)

            val titleTv = row.findViewById<TextView>(R.id.titleTv)
            titleTv.text = "Title: ${mMoviesFound.get(position).title}"

            val yearTv = row.findViewById<TextView>(R.id.yearTv)
            yearTv.text = "Year of production: ${mMoviesFound.get(position).year}"

            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            return row
        }
    }
}