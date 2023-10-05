package au.edu.utas.kit305.tutorial05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

const val SELECT_WEEK: Int = 0

class Mark : AppCompatActivity() {

    //Reference for sppiner (KIT305-607 Self Study - Week 04 - Android)
    private lateinit var ui:ActivityMarkBinding

    var selectedWeek: Int? = 0
    var WeekSpinnerItems = arrayOf("-",
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark)

        ui = ActivityMarkBinding.inflate(layoutInflater)
        setContentView(ui.root)


        //get db connection
        val db = Firebase.firestore
        val studentsCollection = db.collection("students")

        ui.textLoading.text = "Loading..."
        var studentCount = 0
        ui.MarkList.adapter = MarkListAdapter(students = items)

        //vertical list
        ui.MarkList.layoutManager = LinearLayoutManager(this)

        studentsCollection
                .get()
                .addOnSuccessListener { result ->
                    items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all students ---")
                    for (document in result)
                    {
                        Log.d(FIREBASE_TAG, document.toString())
                        val student = document.toObject<Student>()
                        //student.id = document.id
                        Log.d(FIREBASE_TAG, student.toString())

                        items.add(student)

                        (ui.MarkList.adapter as MarkListAdapter).notifyDataSetChanged()
                        studentCount++
                        ui.textLoading.text  = "${studentCount.toString()} Student(s)"
                    }

                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting students list")
                }
        if(studentCount == 0){
            ui.textLoading.text = "No student on your class list"
        }
            //get weeks collection
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
                            // (ui.My.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                        }
                    }
                    .addOnFailureListener {
                        Log.d(FIREBASE_TAG, "Error getting week list")
                    }


        ui.WeekSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, WeekSpinnerItems
        )

        ui.WeekSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id:Long)
            {

              selectedWeek = parent.selectedItemPosition
                //Toast.makeText(applicationContext, "spinner is changed ${selectedWeek}", Toast.LENGTH_SHORT).show()
              Log.d("TAG", selectedWeek.toString()!!)

                ui.MarkList.adapter?.notifyDataSetChanged()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //When click mark button, open mark activity
        ui.buttonAddMark.setOnClickListener{
            val i = Intent(this, MarkSetting::class.java)
            i.putExtra(SELECT_WEEK.toString(), selectedWeek)
            Log.d("TAG SENDING ->", selectedWeek.toString()!!)
            Log.d("TAGSENDING  ->", SELECT_WEEK.toString()!!)
            startActivity(i)
        }

    }
    //onResume is called when the activity is brought back to the foreground.
    override fun onResume() {
        super.onResume()

        ui.MarkList.adapter?.notifyDataSetChanged()
    }

    inner class MarkListHolder(var ui: MyListItemMarkBinding) :
            RecyclerView.ViewHolder(ui.root)

    inner class MarkListAdapter(private val students: MutableList<Student>) :
            RecyclerView.Adapter<MarkListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkListHolder {
            val ui = MyListItemMarkBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MarkListHolder(ui)
        }


        override fun onBindViewHolder(holder: MarkListHolder, position: Int) {
            val student = students[position]   //get the data at the requested position
            var studentName: String = student.given_name.toString() + " " + student.family_name.toString()
            holder.ui.textStudentName.text = studentName
            holder.ui.textStudenttIDM.text = student.studentID.toString()
            selectedweek = ui.WeekSpinner?.selectedItemPosition.toString()

            //set display
            holder.ui.linearAttendance.visibility = View.VISIBLE




            if(selectedweek != "0" && selectedweek!="") {
                ui.textLoading.text = ""
                holder.ui.linearwrap.visibility = View.VISIBLE
                var x = selectedweek.toString().toInt() -1

                var noScoreflg = false
                if (student.grades.size == 0) {
                    //holder.ui.te.text = ""
                    //ui.textAVGResult.text = "No score for ${studentObject.given_name} ${studentObject.family_name}"
                    //todo: not implemented
                }else {
                    noScoreflg = true


                    //ui.MarkList.adapter = MarkListAdapter(students = studentObject.grades)
                }

                if (weeksitems.size != 0 ){
                    if(noScoreflg == true) {
                        var weekObject = Weeks()
                        var selectedWeekname = "week${selectedweek}"
                        var studentResult = student.grades[x]
                        var lpweeksize = weeksitems.size -1
                        for (i in 0..lpweeksize) {
                            if(weeksitems[i].week_name == selectedWeekname)
                            weekObject = weeksitems[i]

                        }

                        // weekly score start!!

                        //start display Attendance (◍•ᴗ•◍)
                        var weeklyResult : Array<Int> = arrayOf()
                        if (!weekObject.marks_type.contains("Attendance")){
                            holder.ui.linearAttendance.visibility = View.GONE
                        } else {
                            //holder.ui.textAttendanceT.text = studentResult.get("Attendance_title")
                            // weekly score start!!
                            holder.ui.linearAttendance.visibility = View.VISIBLE
                            if(studentResult.containsKey("Attendance")) {
                                var result = studentResult.get("Attendance")
                                if (result == null || result == "false") {
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.textAttendanceTscore.isChecked = false
                                    weeklyResult += 0
                                } else {
                                    holder.ui.textAttendanceTscore.isChecked = true
                                    weeklyResult += 100
                                }
                            } else {
                                weeklyResult += 0
                            }
                        }
                        //end  display Attendance (◍•ᴗ•◍)
                        //start display Check box (◍•ᴗ•◍)
                        if (!weekObject.marks_type.contains("CheckPoints")){
                            holder.ui.linearCHK.visibility = View.GONE
                        } else {
                            //holder.ui.textCheckBoxTitle.text = studentResult.get("CheckPoints_title")
                            var check_score = 0
                            holder.ui.linearCHK.visibility = View.VISIBLE
                            if(studentResult.containsKey("CheckBox_MAX")){

                                //get weekly score
                                var max_checkbox = studentResult.get("CheckBox_MAX").toString().toInt()


                                var result = studentResult.get("CheckBox1")
                                if(result == null || result == "false"){
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.checkBox.isChecked = false

                                } else {
                                    holder.ui.checkBox.isChecked = true
                                    check_score = 100/max_checkbox
                                }
                                result = studentResult.get("CheckBox2")
                                if(result == null || result == "false"){
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.checkBox6.isChecked = false

                                } else {
                                    holder.ui.checkBox6.isChecked = true
                                    check_score += 100/max_checkbox
                                }
                                result = studentResult.get("CheckBox3")
                                if(result == null || result == "false"){
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.checkBox7.isChecked = false

                                } else {
                                    holder.ui.checkBox7.isChecked = true
                                    check_score += 100/max_checkbox
                                }
                                result = studentResult.get("CheckBox4")
                                if(result == null || result == "false"){
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.checkBox8.isChecked = false

                                } else {
                                    holder.ui.checkBox8.isChecked = true
                                    check_score += 100/max_checkbox
                                }
                                result = studentResult.get("CheckBox5")
                                if(result == null || result == "false"){
                                    //no marks for this student but other students has mark
                                    //or marked false
                                    holder.ui.checkBox9.isChecked = false

                                } else {
                                    holder.ui.checkBox9.isChecked = true
                                    check_score += 100/max_checkbox
                                }

                                //add on result list
                                weeklyResult += check_score

                                if (max_checkbox == 1){

                                    holder.ui.checkBox6.visibility = View.GONE
                                    holder.ui.checkBox7.visibility = View.GONE
                                    holder.ui.checkBox8.visibility = View.GONE
                                    holder.ui.checkBox9.visibility = View.GONE

                                } else if (max_checkbox == 2){

                                    holder.ui.checkBox6.visibility = View.VISIBLE
                                    holder.ui.checkBox7.visibility = View.GONE
                                    holder.ui.checkBox8.visibility = View.GONE
                                    holder.ui.checkBox9.visibility = View.GONE
                                } else if (max_checkbox == 3) {

                                    holder.ui.checkBox6.visibility = View.VISIBLE
                                    holder.ui.checkBox7.visibility = View.VISIBLE
                                    holder.ui.checkBox8.visibility = View.GONE
                                    holder.ui.checkBox9.visibility = View.GONE
                                }else if (max_checkbox == 4) {

                                    holder.ui.checkBox6.visibility = View.VISIBLE
                                    holder.ui.checkBox7.visibility = View.VISIBLE
                                    holder.ui.checkBox8.visibility = View.VISIBLE
                                    holder.ui.checkBox9.visibility = View.GONE
                                }else if (max_checkbox == 5) {

                                    holder.ui.checkBox6.visibility = View.VISIBLE
                                    holder.ui.checkBox7.visibility = View.VISIBLE
                                    holder.ui.checkBox8.visibility = View.VISIBLE
                                    holder.ui.checkBox9.visibility = View.VISIBLE
                                }
                            } else{
                                // if no marks but other student has
                                weeklyResult += 0
                            }
                        }

                        //end display Check box (◍•ᴗ•◍)
                        //start display Score(◍•ᴗ•◍)
                        if (!weekObject.marks_type.contains("Score")){
                            holder.ui.linearScore.visibility = View.GONE
                        } else {
                            holder.ui.linearScore.visibility = View.VISIBLE
                            if(!studentResult.containsKey("score")){
                                //no marks for this student but other students has mark
                                //or marked false

                                holder.ui.textScoreRsult.text = "0"

                                weeklyResult += 0
                            } else {


                                //holder.ui.textScoreTitle.text = studentResult.get("score_title")
                                var max_score = studentResult.get("score_MAX").toString().toInt()
                                var result = studentResult.get("score")
                                var scoreDisp = "${result}/${max_score}"
                                holder.ui.textScoreRsult.text = scoreDisp

                                var scoreforAVG = (result.toString().toDouble()/max_score.toDouble())*100
                                weeklyResult += scoreforAVG.toInt()
                                //holder.ui.textScoreper.text = scoreforAVG.toString()
                            }

                        }
                        //end display Score(◍•ᴗ•◍)
                        //start display GRADE HD(◍•ᴗ•◍)
                        if (!weekObject.marks_type.contains("GradeHD")){
                            holder.ui.linearGradeHD.visibility = View.GONE
                        } else {
                            holder.ui.linearGradeHD.visibility = View.VISIBLE
                            if(!studentResult.containsKey("GradeHD")){
                                //no marks for this student but other students has mark
                                //or marked false
                                holder.ui.textHDresult.text = "--"
                                weeklyResult += 0
                            } else {
                                //holder.ui.textGradeHD.text = studentResult.get("GradeHD_mark_title")
                                var result = studentResult.get("GradeHD")
                                holder.ui.textHDresult.text = result
                                if (result =="HD+"){
                                    weeklyResult += 100
                                } else if (result == "HD"){
                                    weeklyResult += 80
                                } else if (result == "DN"){
                                    weeklyResult += 70
                                } else if (result == "CR"){
                                    weeklyResult += 60
                                } else if (result == "PP"){
                                    weeklyResult += 50
                                } else if (result == "NN"){
                                    weeklyResult += 0
                                }

                            }

                        }
                        //end display GRADE HD(◍•ᴗ•◍)
                        //start display GRADE ABC(◍•ᴗ•◍)
                        if (!weekObject.marks_type.contains("GradeABC")){
                            holder.ui.linearGradeABC.visibility = View.GONE
                        } else {
                            holder.ui.linearGradeABC.visibility = View.VISIBLE
                            if(!studentResult.containsKey("GradeABC")){
                                //no marks for this student but other students has mark
                                //or marked false
                                holder.ui.textABCresult.text = "--"
                                weeklyResult += 0
                            } else {
                                //holder.ui.textGradeABC.text = studentResult.get("GradeABC_mark_title")
                                var result = studentResult.get("GradeABC")
                                holder.ui.textABCresult.text = result
                                if (result =="A"){
                                    weeklyResult += 100
                                } else if (result == "B"){
                                    weeklyResult += 80
                                } else if (result == "C"){
                                    weeklyResult += 70
                                } else if (result == "D"){
                                    weeklyResult += 60
                                } else if (result == "F"){
                                    weeklyResult += 0
                                }
                            }

                        }


                        //end display GRADE ABC(◍•ᴗ•◍)
                        //calc & display weekly AVG
                        weeklyResult?.let {

                            var weeklyAVG = weeklyResult.average().toString()
                            //weeklyAVG = "weeklyAVG = %.2f".format(weeklyAVG)
                            holder.ui.textAVG.text = weeklyAVG


                        }


                    }
                }











            } else {
                holder.ui.linearwrap.visibility = View.GONE
                ui.textLoading.text = "Plese select week"
            }
        }

        override fun getItemCount(): Int {
            return students.size
        }
    }

}

