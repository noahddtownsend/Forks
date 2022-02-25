package com.noahtownsend.forks.ui

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.COLOR_SCHEME_SYSTEM
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noahtownsend.forks.R
import com.noahtownsend.forks.databinding.ReposListFragmentBinding
import com.noahtownsend.forks.ui.adapter.RepoListAdapter
import com.noahtownsend.forks.viewmodel.ReposListViewModel

class ReposList : Fragment() {
    private val args: ReposListArgs by navArgs()

    private lateinit var viewModel: ReposListViewModel
    private lateinit var binding: ReposListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReposListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[ReposListViewModel::class.java]

        viewModel.orgName = args.orgName

        val repos = viewModel.getReposForOrg(viewModel.orgName)
        val repoListAdapter = RepoListAdapter(mutableListOf())
        repoListAdapter.selectedRepoUrl.subscribe { url ->

            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setColorScheme(COLOR_SCHEME_SYSTEM)
                .build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
        }

        repos.observe(viewLifecycleOwner) { repos ->
            binding.apply {
                repoRecyclerView.layoutManager = LinearLayoutManager(context)
                repoRecyclerView.adapter = repoListAdapter
                repoListAdapter.addAll(repos)

                message.visibility = View.GONE
            }
        }

        viewModel.orgAvatar.observe(viewLifecycleOwner) { avatar ->
            binding.apply {
                orgAvatar.setImageBitmap(avatar)
            }
        }

        viewModel.fullOrgName.observe(viewLifecycleOwner) { fullOrgName ->
            binding.apply {
                orgName.text = fullOrgName
            }
        }

        viewModel.orgDescription.observe(viewLifecycleOwner) { orgDescriptionText ->
            binding.apply {
                orgDescription.text = orgDescriptionText
            }
        }

        viewModel.isNetworkIssue.observe(viewLifecycleOwner) { isNetworkIssue ->
            if (isNetworkIssue) {
                binding.apply {
                    messageImage.setImageResource(R.drawable.alert_circle)
                    messageText.text = requireContext().resources.getString(R.string.network_error)
                    messageProgressbar.visibility = View.GONE

                    message.visibility = View.VISIBLE
                }
            }
        }

        viewModel.isInvalidOrg.observe(viewLifecycleOwner) { isInvalidOrg ->
            if (isInvalidOrg) {
                binding.apply {
                    messageImage.setImageResource(R.drawable.alert_circle)
                    messageText.text =
                        requireContext().resources.getString(
                            R.string.unknown_org,
                            viewModel.orgName
                        )
                    messageProgressbar.visibility = View.GONE

                    message.visibility = View.VISIBLE
                }
            }
        }

        viewModel.orgHasNoRepos.observe(viewLifecycleOwner) { orgHasNoRepos ->
            if (orgHasNoRepos) {
                binding.apply {
                    messageImage.setImageResource(R.drawable.stack)
                    messageProgressbar.visibility = View.GONE
                    messageText.text =
                        requireContext().resources.getString(R.string.no_repos, viewModel.orgName)

                    message.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                findNavController().navigate(ReposListDirections.actionReposListToSearchScreen())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}