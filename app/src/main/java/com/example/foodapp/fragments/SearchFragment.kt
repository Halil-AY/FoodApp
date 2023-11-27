package com.example.foodapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.transition.Transition.ViewAdapter
import com.example.foodapp.R
import com.example.foodapp.activities.CategoryMealsActivity
import com.example.foodapp.activities.MainActivity
import com.example.foodapp.activities.MealActivity
import com.example.foodapp.adapters.MealsAdapter
import com.example.foodapp.databinding.FragmentSearchBinding
import com.example.foodapp.pojo.Meal
import com.example.foodapp.pojo.MealList
import com.example.foodapp.viewModel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.log


class SearchFragment : Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var viewModel:HomeViewModel
    private lateinit var searchRecyclerViewAdapter: MealsAdapter
    private lateinit var meal: Meal
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareRecyclerView()
        binding.imgSearchArrow.setOnClickListener{ searchMeals()}

        observeSearchedMealsLiveData()


        // arama butonuna tıklamaya ihtiyaç duymadan otomatik arama yaptırmak için
        var searchJob: Job?=null
        binding.edSearchBox.addTextChangedListener {searchQuery->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500) // 500 kadar bekle ve ara
                viewModel.searchMeals(searchQuery.toString())
            }
        }
    }




    private fun observeSearchedMealsLiveData() {
        viewModel.observeSearchedMealsLiveData().observe(viewLifecycleOwner, Observer { mealList->
            searchRecyclerViewAdapter.differ.submitList(mealList)

        })
    }

    private fun searchMeals() {
        val searchQuery = binding.edSearchBox.text.toString()
        if (searchQuery.isNotEmpty()){
            viewModel.searchMeals(searchQuery)

        }
    }

    private fun prepareRecyclerView() {
        searchRecyclerViewAdapter = MealsAdapter()
        binding.rvSearchedMeals.apply {
            layoutManager = GridLayoutManager(context,2,GridLayoutManager.VERTICAL,false)
            adapter = searchRecyclerViewAdapter
        }

    }

}