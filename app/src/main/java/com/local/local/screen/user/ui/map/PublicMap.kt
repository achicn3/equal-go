package com.local.local.screen.user.ui.map

import android.content.Intent
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
import com.local.local.manager.UserLoginManager
import com.local.local.util.PermissionRationalActivity
import com.local.local.util.PermissionUtil

class PublicMap : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!PermissionUtil.hasGrantedLocation(context)){
            startActivity(Intent(context,PermissionRationalActivity::class.java))
        }
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
        val nowLan = UserLoginManager.instance.userData?.latitude ?: 23.06382
        val nowLng = UserLoginManager.instance.userData?.longitude ?: 120.41593
        googleMap?.addMarker(MarkerOptions().position(LatLng(nowLan,nowLng)).title("上一次所在位置"))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(nowLan,nowLng),18.0f))
    }
}