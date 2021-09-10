package com.bignerdranch.android.mapboxplayground


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlin.math.floor


class StudySpaces : AppCompatActivity() {
    private lateinit var studySpacesList: ListView
    private var mapRepository: MapRepository = MapRepository.get()

    ////////////////////// LIFECYCLE METHODS ////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_spaces)

        initUiElements()
        updateStudySpacesList()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_BUILDING_INFO) {
            val id = data?.getStringExtra(DIRECTIONS_REQUESTED_FOR_ID).toString()
            setDirectionsRequested(id)
        }
    }

    ////////////////////// OTHER METHODS ////////////////////////////

    private fun setDirectionsRequested(buildingId: String) {
        val data = Intent().apply {
            putExtra(DIRECTIONS_REQUESTED_FOR_ID, buildingId)
        }
        setResult(Activity.RESULT_OK, data)
        this.finish()
    }

    private fun updateStudySpacesList() {
        mapRepository.buildings.observe(this) { buildings ->
            var studySpaces = buildings.filter { building -> building.type == "study" }

            val adapter =
                StudySpacesAdapter(this, android.R.layout.simple_list_item_1, studySpaces)

            studySpacesList.adapter = adapter
        }
    }

    private fun initUiElements() {
        studySpacesList = findViewById(R.id.studySpacesList)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Study Spaces"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun startBuildingDetailsActivity(buildingId: String) {
        if (buildingId.isNotEmpty()) {
            val intent = BuildingDetailsActivity.newIntent(this@StudySpaces, buildingId)
            startActivityForResult(intent, REQUEST_CODE_BUILDING_INFO)
        }
    }

    private fun calculateOccupancyInPercents(building: Building): Int {
        var occupancyInPercent: Int = 0

        var sumPercent = 0;
        building.people.forEach { floor ->
            sumPercent += floor
        }

        Log.d("OCCUPANCY", building.people.toString())

        occupancyInPercent = floor((sumPercent.toDouble() / building.people.size)).toInt()

        return occupancyInPercent
    }

    inner class StudySpacesAdapter(
        context: Context,
        layoutResource: Int,
        private val studySpaces: List<Building>
    ) : ArrayAdapter<Building>(context, layoutResource, studySpaces) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = context
                .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val rowView: View = inflater.inflate(R.layout.study_space_item, parent, false)

            val buildingImage = rowView.findViewById<ImageView>(R.id.buildingImage)
            val buildingName = rowView.findViewById<TextView>(R.id.buildingName)
            val buildingOccupancy = rowView.findViewById<TextView>(R.id.buildingOccupancy)
            val progressBar = rowView.findViewById<ProgressBar>(R.id.progressBar)

            val currentBuilding = studySpaces[position]

            val totalBuildingOccupancy: Int = calculateOccupancyInPercents(currentBuilding)



            Picasso.get().load(currentBuilding.url).into(buildingImage)
            buildingName.text = currentBuilding.buildingName
            buildingOccupancy.text = "Occupancy: $totalBuildingOccupancy%"

            progressBar.progress = totalBuildingOccupancy

            rowView.setOnClickListener {
                startBuildingDetailsActivity(currentBuilding.id)
            }

            return rowView
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, StudySpaces::class.java)
        }
    }
}