package xyz.droidev.eventsync.ui.vote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import xyz.droidev.eventsync.adapter.PoliticalPartyAdapter
import xyz.droidev.eventsync.data.api.ApiService
import xyz.droidev.eventsync.data.api.PoliticalParty
import xyz.droidev.eventsync.data.api.SubmitVoteBody
import xyz.droidev.eventsync.databinding.FragmentSecondBinding
import javax.inject.Inject

@AndroidEntryPoint
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy{
        PoliticalPartyAdapter(emptyList())
    }
    @Inject
    lateinit var apiService: ApiService
    private var selectedParty: PoliticalParty? = null
    val args by navArgs<SecondFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvParty.adapter = adapter
        binding.rvParty.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        adapter.setOnItemClickListener {
            selectedParty = it
        }
        lifecycleScope.launch {
            val data = apiService.getPoliticalParty()
            adapter.setData(data)
        }

        binding.generateButton.setOnClickListener {
            if(selectedParty == null){
                Toast.makeText(requireContext(),"Please select someone to vote",Toast.LENGTH_LONG).show()
            }else{
                lifecycleScope.launch {
                    apiService.submitVote(
                        SubmitVoteBody(
                            args.userId,
                            party_code = selectedParty!!.party_code,
                            party_name = selectedParty!!.party_name
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@HiltViewModel
class SecondViewModel @Inject constructor(
    private val apiService: ApiService
): ViewModel(){

    init{

    }

    private fun getParties(){
        viewModelScope.launch {
            apiService.getPoliticalParty()
        }
    }

    data class SecondFragmentUiState(
        val parties: List<PoliticalParty>,
    )

}

