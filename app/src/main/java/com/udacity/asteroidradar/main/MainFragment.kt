package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.bindImageOfTheDayToImageView
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.list_asteroid_item.*

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val myAdapter = AsteroidAdapter(AsteroidAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it)
        })

        binding.asteroidRecycler.adapter = myAdapter

        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
        })

        /*viewModel.asteroidItems.observe(viewLifecycleOwner) { asteroidList ->
            myAdapter.submitList(asteroidList)
        }*/

        viewModel.asteroids.observe(viewLifecycleOwner) { asteroid ->
            myAdapter.submitList(asteroid)
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_all_menu -> viewModel.optionMenu.value = Options.SHOW_WEEK
            R.id.show_rent_menu -> viewModel.optionMenu.value = Options.SHOW_TODAY
            R.id.show_buy_menu -> viewModel.optionMenu.value = Options.SHOW_ALL
        }
        return super.onOptionsItemSelected(item)
    }
}
