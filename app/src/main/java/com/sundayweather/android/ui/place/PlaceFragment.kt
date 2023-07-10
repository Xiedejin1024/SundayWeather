package com.sundayweather.android.ui.place

import android.Manifest
import android.content.Intent
import android.location.Address
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.sundayweather.android.MyApplication
import com.sundayweather.android.activity.MainActivity
import com.sundayweather.android.activity.WeatherActivity
import com.sundayweather.android.databinding.FragmentPlaceBinding
import com.sundayweather.android.utils.getLatAndLng
import com.sundayweather.android.utils.showToast

class PlaceFragment : Fragment() {
    val viewModel by lazy { ViewModelProvider(requireActivity()).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter
    private var _binding: FragmentPlaceBinding? = null
    val binding get() = _binding!!
    lateinit var addressList: List<Address>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //申请权限获取当前定位信息
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "PermissionX需要您同意以下权限才能正常使用",
                    "确定",
                    "取消"
                )
            })
            .request(RequestCallback { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    if (!viewModel.isPlaceSaved()) {
                        getLatAndLng { address ->
                            binding.editSearchPlace.setText(address)
                        }
                    }
                } else {
                    "您拒绝了如下权限：$deniedList".showToast(MyApplication.context)
                }
            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavePlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclView.adapter = adapter

        binding.editSearchPlace.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                binding.recyclView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        binding.btnLocation.setOnClickListener {
            getLatAndLng { address ->
                binding.editSearchPlace.setText(address)
            }
        }

        viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
            val place = result.getOrNull()
            if (place != null) {
                binding.recyclView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                adapter.notifyDataSetChanged()
            } else {
                "没有查询到任何地点".showToast(MyApplication.context)
                result.exceptionOrNull()?.printStackTrace()
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
