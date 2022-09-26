package de.erikspall.mensaapp.ui.canteenlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import de.erikspall.mensaapp.databinding.FragmentCanteenListBinding
import de.erikspall.mensaapp.domain.const.MaterialSizes
import de.erikspall.mensaapp.domain.usecases.foodprovider.GetOpeningHoursAsString
import de.erikspall.mensaapp.domain.utils.Extensions.pushContentUpBy
import de.erikspall.mensaapp.domain.utils.HeightExtractor
import de.erikspall.mensaapp.ui.adapter.FoodProviderCardAdapter
import de.erikspall.mensaapp.ui.canteenlist.viewmodel.CanteenListViewModel
import de.erikspall.mensaapp.ui.canteenlist.viewmodel.event.CanteenListEvent

@AndroidEntryPoint
class CanteenListFragment : Fragment() {
    private var _binding: FragmentCanteenListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: CanteenListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialElevationScale(true).apply {
            duration = 150L
        }
        exitTransition = MaterialElevationScale(false).apply {
            duration = 100L
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 150L
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.recyclerViewCanteen.setHasFixedSize(true)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCanteenListBinding.inflate(inflater, container, false)
        val root: View = binding.root



        binding.recyclerViewCanteen.pushContentUpBy(
            HeightExtractor.getNavigationBarHeight(requireContext()) +
                    MaterialSizes.BOTTOM_NAV_HEIGHT
        )

        val adapter = FoodProviderCardAdapter(
            requireContext(),
            findNavController()
        )

        binding.recyclerViewCanteen.adapter = adapter

        //binding.recyclerViewCanteen.setHasFixedSize(true)

        viewModel.onEvent(CanteenListEvent.CheckIfNewLocationSet)

        viewModel.canteens.observe(viewLifecycleOwner) { canteens ->
            if (canteens.isEmpty()) {
                // TODO: try to retrieve data
                binding.lottieContainer.visibility = VISIBLE
                binding.libraryAppbarLayout.visibility = INVISIBLE
                binding.libraryNestedScroll.visibility = INVISIBLE
            } else {
                binding.lottieContainer.visibility = GONE
                binding.libraryAppbarLayout.visibility = VISIBLE
                binding.libraryNestedScroll.visibility = VISIBLE

            }
            Log.d("CanteenListFragment", "Canteens: $canteens")
            canteens.let {
                adapter.submitList(it.filter { foodProvider ->
                    foodProvider.location.name == requireContext().getString(viewModel.state.showingLocation.getValue())
                })
            }
        }



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}