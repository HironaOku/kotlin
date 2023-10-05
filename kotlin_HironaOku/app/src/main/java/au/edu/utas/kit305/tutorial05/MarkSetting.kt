package au.edu.utas.kit305.tutorial05

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import au.edu.utas.kit305.tutorial05.databinding.ActivityMarkSettingBinding
import au.edu.utas.kit305.tutorial05.databinding.ActivityStudentListBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

val SELECTED_MARK_STYLE : Array<String> = arrayOf()


//var SELECTED_STYLE = Array<String>
//data class SelectedStyle(val Week: String, val MarkTitle: String, val MarkStyle: String, val OptionalValue: String)
var selectedweek = ""
class MarkSetting : AppCompatActivity() {

    private lateinit var ui: ActivityMarkSettingBinding
    var checkedStyle = ""
    var optionValue = ""
    var WeekSpinnerItems = arrayOf("",
        "Week 1",
        "Week 2",
        "Week 3",
        "Week 4",
        "Week 5",
        "Week 6",
        "Week 7",
        "Week 8",
        "Week 9",
        "Week 10",
        "Week 11",
        "Week 12")

    var CkeckPointSppinerItems = arrayOf(
        "1",
        "2",
        "3",
        "4",
        "5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMarkSettingBinding.inflate(layoutInflater)
        setContentView(ui.root)


        var selectedWeek: Int? = intent.getIntExtra(SELECT_WEEK.toString(), 0)
        //var weekObject = items[selectedWeek]

        //get weeks mark type
        val db = Firebase.firestore
        val weeksCollection = db.collection("weeks")
        weeksCollection
                .get()
                .addOnSuccessListener { result ->
                    weeksitems.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all students ---")
                    for (document in result)
                    {
                        Log.d(FIREBASE_TAG, document.toString())
                        val weeks = document.toObject<Weeks>()
                        weeks.week_name = document.id
                        Log.d(FIREBASE_TAG, weeks.toString())

                        weeksitems.add(weeks)

                        //
                        // (ui.My.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }


        if (selectedWeek != null || selectedWeek != 0) {
            Log.d("TAG*****", selectedWeek.toString())
            //display what can selected
            val weekName = "week${selectedWeek}"
            Log.d("weekName", weekName)

            var weekObject : Weeks = Weeks()
            var weekItemsize = weeksitems?.size
            if (weekItemsize != 0) {
                for (x in 0..weekItemsize) {
                    var wkobje = weeksitems[x].week_name
                    if (wkobje == weekName) {
                        weekObject = weeksitems[x]
                        //set invisible if already marked !!!!!!
                        if (weekObject.marks_type.contains("Attendance")) {
                            ui.radioAttendance.visibility = View.GONE
                        }
                        if (weekObject.marks_type.contains("Score")){
                            ui.radioScore.visibility = View.GONE
                        }
                        if (weekObject.marks_type.contains("CheckPoints")){
                            ui.radioMltCheckPoints.visibility = View.GONE
                        }
                        if (weekObject.marks_type.contains("GradeHD")){
                            ui.radioGradeHD.visibility = View.GONE
                        }
                        if (weekObject.marks_type.contains("GradeABC")){
                            ui.radioGradeABC.visibility = View.GONE
                        }

                        break
                    }
                }
            }
        }

        //from  SpinnerSample
        ui.spinnerWeek.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            WeekSpinnerItems
        )
        //set selected week if user selected in Mark Activity
        selectedWeek?.let{
            //ui.spinnerWeek.setSelection(selectedWeek!!);


        }

        //set spinner item for check pox option
        ui.spinnerMltCheck.adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            CkeckPointSppinerItems
        )


        ui.spinnerMltCheck.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                Log.d("TAG", selectedItem)
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        //set visibility for option of multiple check points and score
        ui.linearMltCheckPoint.visibility = View.GONE
        ui.linearScore.visibility = View.GONE


        //when click the add student button move to add activity
        ui.buttonCreateMark.setOnClickListener{
            selectedweek = ui.spinnerWeek?.selectedItemPosition.toString()
            if(selectedweek != "0" && selectedweek!="") {
            val i = Intent(this, Marking::class.java)
            var checkSlected = checkSelect(checkedStyle)

                if(checkSlected) {
                    var selectedStyle = arrayOf(selectedweek, checkedStyle, optionValue)
                    //var Selecteddata:Array<String> = arrayOf(checkedStyle,checkedStyle)
                    i.putExtra(SELECTED_MARK_STYLE.toString(), selectedStyle)
                    Log.d("MESSAGE", selectedStyle.toString())
                    startActivity(i)
                    finish()
                }
            //get week marktype item in selected weeks

   /*         val db = Firebase.firestore
            val weeksCollection = db.collection("weeks")
            weeksCollection
                    .get()
                    .addOnSuccessListener { result ->
                        weeksitems.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                        Log.d(FIREBASE_TAG, "--- all students ---")
                        for (document in result)
                        {
                            Log.d(FIREBASE_TAG, document.toString())
                            val weeks = document.toObject<Weeks>()
                            weeks.week_name = document.id
                            Log.d(FIREBASE_TAG, weeks.toString())

                            weeksitems.add(weeks)

                            //
                        // (ui.My.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                        }


                    }
                    .addOnFailureListener {
                        Log.d(FIREBASE_TAG, "Error getting week list")
                    }
*/

                var weekName = "week${selectedweek.toString().toInt()}"
                var weekObject: Weeks = Weeks()
                var weekItemsize = weeksitems?.size
                var sizeForloop = weekItemsize - 1
                if (weekItemsize != 0) {
                    for (x in 0..sizeForloop) {

                        var wkobje = weeksitems[x].week_name
                        if (wkobje == weekName) {
                            weekObject = weeksitems[x]
                            if (!weekObject.marks_type.contains(checkedStyle)) {
                                weekObject.marks_type.add(checkedStyle!!)
                            }
                            weekObject.week_name = weekName
                            break
                        } else {
                            weekObject.marks_type = mutableListOf(checkedStyle!!)
                            weekObject.week_name = weekName
                        }
                    }
                }

                //result3.set(checkedStyle)
                //var result4 = weekObject.get(weekNum)
                //var result3 = weekObject.marks_type.get(weekNum)

                val db = Firebase.firestore
                val weeksCollection = db.collection("weeks")

                val data = weekObject

                if (data != null) {
                    weeksCollection.document("${weekName.toString()}")
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener {
                                Log.d(FIREBASE_TAG, "Successfully updated weeks checkstyle as ${checkedStyle}")
                                Toast.makeText(applicationContext, "New marking schema created!!", Toast.LENGTH_SHORT).show()
                                finish()

                            }
                            .addOnFailureListener { exception ->
                                Log.d(FIREBASE_TAG, "Error getting documents: ", exception)
                            }
                }
            } else {
                ui.textSelectWeek.text = "PLEASE select week"
            }


        }


//week spinner
        ui.spinnerWeek.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                selectedWeek = position

                if(position != 0){


                //display what can selected
                val weekName = "week${position}"
                Log.d("weekName", weekName)

                var weekObject : Weeks = Weeks()
                var weekItemsize = weeksitems?.size-1
                    ui.radioAttendance.visibility = View.VISIBLE
                    ui.radioScore.visibility = View.VISIBLE
                    ui.radioMltCheckPoints.visibility = View.VISIBLE
                    ui.radioGradeHD.visibility = View.VISIBLE
                    ui.radioGradeABC.visibility = View.VISIBLE
                if (weekItemsize != 0) {
                    for (x in 0..weekItemsize) {
                        var wkobje = weeksitems[x].week_name
                        if (wkobje == weekName) {
                            weekObject = weeksitems[x]
                            //set invisible if already marked !!!!!!
                            if (weekObject.marks_type.contains("Attendance")) {
                                ui.radioAttendance.visibility = View.GONE
                            }
                            if (weekObject.marks_type.contains("Score")){
                                ui.radioScore.visibility = View.GONE
                            }
                            if (weekObject.marks_type.contains("CheckPoints")){
                                ui.radioMltCheckPoints.visibility = View.GONE
                            }
                            if (weekObject.marks_type.contains("GradeHD")){
                                ui.radioGradeHD.visibility = View.GONE
                            }
                            if (weekObject.marks_type.contains("GradeABC")){
                                ui.radioGradeABC.visibility = View.GONE
                            }

                            break
                        }
                    }
                }
            }
            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


    }

    //Validation check
    private fun checkSelect(checkedStyle:String): Boolean {
        var checkFlg : Boolean = true
        if (checkedStyle == ""){
            ui.textMarkTypeTitle.text = "REQUIRED: Select the mark type"
            ui.textMarkTypeTitle.setTextColor(Color.RED)
            checkFlg = false}

        if (checkedStyle == "CheckPoints") {
            var optionValues = ui.spinnerMltCheck?.selectedItemPosition + 1
            optionValue = optionValues.toString()
            checkFlg = true
        }
        if (checkedStyle == "Score") {
            var length = ui.editTextNumber.toString()?.length ?: 0

           // ui.editTextNumber.text.toString().toInt()?.let {
                //Log.d("taggg", ui.editTextNumber.text.toString())
                //setscore = ui.editTextNumber.text.toString().toInt()
                //Log.d("setscore", setscore.toString())
                if(length == 0){
                    ui.textOutOfScore.text = "REQUIRED: Enter maximum score? (1~100)"
                    ui.textOutOfScore.setTextColor(Color.RED)
                    checkFlg = false
                }else if (ui.editTextNumber?.text.toString().toInt() in 1..100){
                    checkFlg = true
                    optionValue = ui.editTextNumber?.text.toString()
                } else{
                    ui.textOutOfScore.text = "REQUIRED: Enter maximum score? (1~100)"
                    ui.textOutOfScore.setTextColor(Color.RED)
                    checkFlg = false
                }


        }

        if (checkedStyle == "GradeHD" || checkedStyle == "GradeABC") {
            optionValue = "0"
        }

        if (ui.spinnerWeek.selectedItemPosition < 1) {

            ui.textSelectWeek.text = "REQUIRED: Select the week"
            ui.textSelectWeek.setTextColor(Color.RED)
            checkFlg = false

        } else {
            selectedweek = ui.spinnerWeek.selectedItemPosition.toString()
        }
        return checkFlg

    }

    //https://developer.android.com/guide/topics/ui/controls/radiobutton
    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            var checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioAttendance ->
                    if (checked) {
                        Log.d("CHECKED", "Attendance")
                        ui.linearMltCheckPoint.visibility = View.GONE
                        ui.linearScore.visibility = View.GONE
                        checkedStyle = "Attendance"
                    }
                R.id.radioMltCheckPoints ->
                    if (checked) {
                        ui.linearMltCheckPoint.visibility = View.VISIBLE
                        ui.linearScore.visibility = View.GONE
                        checkedStyle = "CheckPoints"
                    }
                R.id.radioScore ->
                    if (checked) {
                        ui.linearMltCheckPoint.visibility = View.GONE
                        ui.linearScore.visibility = View.VISIBLE
                        checkedStyle = "Score"
                    }
                R.id.radioGradeHD ->
                    if (checked) {
                        ui.linearMltCheckPoint.visibility = View.GONE
                        ui.linearScore.visibility = View.GONE
                        checkedStyle = "GradeHD"
                    }
                R.id.radioGradeABC ->
                    if (checked) {
                        ui.linearMltCheckPoint.visibility = View.GONE
                        ui.linearScore.visibility = View.GONE
                        checkedStyle = "GradeABC"
                    }
            }
        }
    }
}

