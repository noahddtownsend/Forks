package com.noahtownsend.forks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.noahtownsend.forks.databinding.SearchScreenFragmentBinding

class SearchScreen : Fragment() {
    private lateinit var binding: SearchScreenFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchScreenFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            searchButton.setOnClickListener {
                navigateToResults(searchBox.text.toString())
            }

            searchBox.setOnEditorActionListener { v, actionId, event ->
                return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    navigateToResults(searchBox.text.toString())
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun navigateToResults(orgName: String) {
        findNavController().navigate(
            SearchScreenDirections.actionSearchScreenToReposList(orgName)
        )
    }

}