package com.dililp.myvideo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dililp.myvideo.databinding.FragmentFoldersBinding
import com.dililp.myvideo.databinding.FragmentVideosBinding

class FoldersFragment : Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.themesList[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_folders, container, false)
        val binding = FragmentFoldersBinding.bind(view)

        binding.FolderRecycler.setHasFixedSize(true)
        binding.FolderRecycler.setItemViewCacheSize(10)
        binding.FolderRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.FolderRecycler.adapter = FolderAdapter(requireContext(),MainActivity.folderList)
        binding.totalFolders.text = "Total Folders : ${MainActivity.folderList.size}"
        return view
    }


}