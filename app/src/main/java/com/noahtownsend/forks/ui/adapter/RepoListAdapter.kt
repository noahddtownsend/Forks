package com.noahtownsend.forks.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noahtownsend.forks.Repo
import com.noahtownsend.forks.databinding.RepoListItemBinding
import io.reactivex.rxjava3.subjects.PublishSubject

class RepoListAdapter(private var repos: MutableList<Repo>) :
    ListAdapter<Repo, RepoListAdapter.RepoViewHolder>(DiffCallback) {

    val selectedRepoUrl: PublishSubject<String> = PublishSubject.create()

    fun addAll(repos: List<Repo>) {
        this.repos.addAll(repos)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder(
            RepoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        return holder.bind(repos[position], selectedRepoUrl)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.name == newItem.name
                        && oldItem.description == newItem.description
                        && oldItem.url == newItem.url
                        && oldItem.numStars == newItem.numStars
            }

        }
    }

    class RepoViewHolder(private var binding: RepoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo, onItemSelected: PublishSubject<String>) {
            binding.apply {
                title.text = repo.name
                description.text = repo.description
                starCount.text = repo.numStars.toString()

                root.setOnClickListener {
                    onItemSelected.onNext(repo.url)
                }
            }
        }
    }
}