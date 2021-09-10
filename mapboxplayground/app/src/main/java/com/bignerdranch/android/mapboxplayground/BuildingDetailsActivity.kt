package com.bignerdranch.android.mapboxplayground

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import kotlin.math.floor

const val DIRECTIONS_REQUESTED_FOR_ID = "com.bignerdranch.android.densitydodger.directions_requested"

class BuildingDetailsActivity : AppCompatActivity() {
    private val mapRepository: MapRepository = MapRepository.get();
    private var building: Building? = null
    private lateinit var buildingNameText: TextView
    private lateinit var densityLevelText: TextView
    private lateinit var densityLevelPercentage: TextView

    private lateinit var getDirectionsBtn: Button
    private lateinit var buildingImageView: ImageView
    private lateinit var densityProgressBar: ProgressBar


    //////////////////////// LIFECYCLE ////////////////////////

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_details)

        getDataFromIntent()
        initializeUiElements()
    }

    //////////////////////// OTHER FCNS ////////////////////////

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeUiElements() {
        val totalBuildingOccupancy: Int = calculateOccupancyInPercents()

        getDirectionsBtn = findViewById(R.id.getDirectionsBtn)
        getDirectionsBtn.setOnClickListener {
            building?.let {
                setDirectionsRequested(it.id)
            }
        }

        buildingImageView = findViewById(R.id.imageView)
        buildingNameText = findViewById(R.id.buildingName)
        densityLevelText = findViewById(R.id.density_level)
        densityProgressBar = findViewById(R.id.progressBar)
        densityLevelPercentage = findViewById(R.id.occupancyLevel)

        Picasso.get().load(building?.url).into(buildingImageView)

        buildingNameText.text = "${building?.buildingName}"

        when (building?.densityLevel) {
            3 -> {
                densityLevelText.text = "High"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    densityProgressBar.progressDrawable.colorFilter =
                        BlendModeColorFilter(Color.RED,BlendMode.SRC_IN)
                }
                densityLevelPercentage.text = "$totalBuildingOccupancy" + "%"
                densityProgressBar.setProgress(totalBuildingOccupancy!!,false)


            }
            2 -> {
                densityLevelText.text = "Moderate"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    densityProgressBar.progressDrawable.colorFilter =
                        BlendModeColorFilter(Color.YELLOW,BlendMode.SRC_IN)
                }
                densityLevelPercentage.text = "$totalBuildingOccupancy" + "%"
                densityProgressBar.setProgress(totalBuildingOccupancy!!,false)

            }
            else -> {
                densityLevelText.text = "Low"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    densityProgressBar.progressDrawable.colorFilter =
                        BlendModeColorFilter(Color.GREEN,BlendMode.SRC_IN)
                }
                densityLevelPercentage.text = "$totalBuildingOccupancy" + "%"
                densityProgressBar.setProgress(totalBuildingOccupancy!!,false)

            }
        }

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "${building?.buildingName}"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    private fun setDirectionsRequested(buildingId: String) {
        val data = Intent().apply {
            putExtra(DIRECTIONS_REQUESTED_FOR_ID, buildingId)
        }
        setResult(Activity.RESULT_OK, data)
        this.finish()
    }

    private fun getDataFromIntent() {
        val buildingId = intent.getStringExtra(BUILDING_ID).toString()

        building = mapRepository.getBuildingFromId(buildingId)
    }

    companion object {
        fun newIntent(packageContext: Context, buildingId: String): Intent {
            return Intent(packageContext, BuildingDetailsActivity::class.java).apply {
                putExtra(BUILDING_ID, buildingId)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun calculateOccupancyInPercents(): Int {
        var occupancyInPercent: Int = 0

        var sumPercent = 0;
        building?.people?.forEach { floor ->
            sumPercent += floor
        }

        Log.d("OCCUPANCY", building?.people.toString())

        occupancyInPercent = floor((sumPercent.toDouble() / (building?.people?.size ?: 0))).toInt()

        return occupancyInPercent
    }
}