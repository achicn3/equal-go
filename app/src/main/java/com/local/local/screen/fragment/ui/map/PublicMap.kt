package com.local.local.screen.fragment.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.local.local.R
import com.local.local.extensions.Extensions.locationList
import com.local.local.manager.LoginManager

class PublicMap : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.map)
        mapView?.onCreate(savedInstanceState)
        mapView?.onResume()
        mapView?.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        locationList.forEach { location ->
            googleMap?.addMarker(
                    MarkerOptions()
                            .position(
                                    LatLng(location.latitude, location.Longitude)
                            )
                            .title(location.name)
            )
        }
        val nowLan = LoginManager.instance.userData?.latitude ?: 23.06382
        val nowLng = LoginManager.instance.userData?.longitude ?: 120.41593
        googleMap?.addMarker(MarkerOptions().position(LatLng(nowLan,nowLng)).title("上一次所在位置"))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nowLan,nowLng),18.0f))
    }
}