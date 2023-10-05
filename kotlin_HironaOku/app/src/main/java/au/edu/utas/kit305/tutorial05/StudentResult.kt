package au.edu.utas.kit305.tutorial05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

const val STUDENT_ID = "Student_id"
var studentID : Int? = null
var i = 0
var shareRsult: String? ="RESULT"
class StudentResult : AppCompatActivity() {

    private lateinit var ui: ActivityStudentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //setContentView(R.layout.activity_student_result)
        ui = ActivityStudentResultBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //read and set selected student information
        studentID = intent.getIntExtra(STUDENT_INDEX, -1)
        var studentObject = items[studentID!!]

        //set student information on display
        ui.textFamilyName2.text = studentObject.family_name
        ui.textFirstName.text = studentObject.given_name
        ui.textStudentID2.text = studentObject.studentID.toString()
        var noScoreflg = false
        if (studentObject.grades.size == 0) {
                ui.textAGV.text = ""
                ui.textAVGResult.text = "No score for ${studentObject.given_name} ${studentObject.family_name}"

        }else {
            noScoreflg = true


        }

        if(noScoreflg == true) {
            ui.MyStudentRsultList.adapter = StudentListResultAdapter(students = studentObject.grades)

        }
            //vertical list
            ui.MyStudentRsultList.layoutManager = LinearLayoutManager(this)

        //get weeks mark type
        /*val db = Firebase.firestore
        val weeksCollection = db.collection("weeks")
        weeksCollection
                .get()
                .addOnSuccessListener { result ->
                    weeksitems.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all weeks ---")
                    for (document in result)
                    {
                        Log.d(FIREBASE_TAG, document.toString())
                        val weeks = document.toObject<Weeks>()
                        //weeks.week_name = document.id
                        Log.d(FIREBASE_TAG, weeks.toString())

                        weeksitems.add(weeks)

                        //(ui.My.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }*/
        /*//get db connection
        val db = Firebase.firestore
        val studentsCollection = db.collection("students")

        studentsCollection
                .get()
                .addOnSuccessListener { result ->
                    items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                    Log.d(FIREBASE_TAG, "--- all students ---")
                    for (document in result)
                    {
                        Log.d(FIREBASE_TAG, document.toString())
                        val student = document.toObject<Student>()
                        student.id = document.id
                        Log.d(FIREBASE_TAG, student.toString())

                        items.add(student)

                        (ui.MyStudentRsultList.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting students list")
                }*/

        //When click mark button, open student edit activity
        ui.buttonEdit.setOnClickListener{
            val i = Intent(this, EditStudent::class.java)

            i.putExtra(STUDENT_INDEX, studentID!!)
            startActivity(i)
        }

        //calc total AVG
        if(noScoreflg == true){


        if (weeksitems.size != 0 ){

            var weekObject = Weeks()
            var weeklyAVG : Array<Int> = arrayOf()
            var studentObject = items[studentID!!]
            var lpweeksize = weeksitems.size -1
            for (x in 0..lpweeksize){

                weekObject = weeksitems[x]


                var studentResult = studentObject.grades[x]

                // weekly score start!!
                shareRsult += " ◆   ${weekObject.week_name}  ◆ "
                //start display Attendance (◍•ᴗ•◍)
                if (weekObject.marks_type.contains("Attendance")){
                    if(studentResult.containsKey("Attendance")) {
                        var result = studentResult.get("Attendance")
                        if (result == null || result == "false") {
                            //no marks for this student but other students has mark
                            //or marked false
                            shareRsult += "Attendance: 0 ,"
                            weeklyAVG += 0
                        } else {
                            shareRsult += "Attendance: 100 ,"
                            weeklyAVG += 100
                        }
                    } else {
                        shareRsult += "Attendance: 0 ,"
                        weeklyAVG += 0
                    }
                }
                //end  display Attendance (◍•ᴗ•◍)
                //start display Check box (◍•ᴗ•◍)
                if (weekObject.marks_type.contains("CheckPoints")){
                    var check_score = 0
                    if(studentResult.containsKey("CheckBox_MAX")){

                        //get weekly score
                        var max_checkbox = studentResult.get("CheckBox_MAX").toString().toInt()


                        var result = studentResult.get("CheckBox1")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false


                        } else {

                            check_score = 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox2")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false

                        } else {
                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox3")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false

                        } else {
                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox4")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false

                        } else {

                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox5")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false

                        } else {
                            check_score += 100/max_checkbox
                        }
                        shareRsult += "Check point : ${check_score.toString()} ,"
                        //add on result list
                        weeklyAVG += check_score


                    } else{
                        // if no marks but other student has
                        shareRsult += "Check point : 0 ,"
                        weeklyAVG += 0
                    }
                }

                //end display Check box (◍•ᴗ•◍)

                //start display Score(◍•ᴗ•◍)
                if (weekObject.marks_type.contains("Score")){

                    if(!studentResult.containsKey("score")){
                        //no marks for this student but other students has mark
                        //or marked false
                        shareRsult += "Score : 0 ,"
                        weeklyAVG += 0
                    } else {
                        var max_score = studentResult.get("score_MAX").toString().toInt()
                        var result = studentResult.get("score")

                        var scoreforAVG = (result.toString().toDouble()/max_score.toDouble())*100
                        weeklyAVG += scoreforAVG.toInt()
                        //holder.ui.textScoreper.text = scoreforAVG.toString()
                        shareRsult += "Score : ${result.toString()}/${max_score.toString()}(${scoreforAVG.toString()}%) ,"
                    }

                }
                //end display Score(◍•ᴗ•◍)

                //start display GRADE HD(◍•ᴗ•◍)
                if (weekObject.marks_type.contains("GradeHD")){

                    if(!studentResult.containsKey("GradeHD")){
                        //no marks for this student but other students has mark
                        //or marked false
                        shareRsult += "Grade(HD,DN...) :  -- (0) ,"
                        weeklyAVG += 0
                    } else {
                        var result = studentResult.get("GradeHD")

                        if (result =="HD+"){
                            weeklyAVG += 100
                        } else if (result == "HD"){
                            weeklyAVG += 80
                        } else if (result == "DN"){
                            weeklyAVG += 70
                        } else if (result == "CR"){
                            weeklyAVG += 60
                        } else if (result == "PP"){
                            weeklyAVG += 50
                        } else if (result == "NN"){
                            weeklyAVG += 0
                        }
                        shareRsult += "Grade(HD,DN...) : ${result.toString()} (${weeklyAVG.toString()}) ,"
                    }

                }
                //end display GRADE HD(◍•ᴗ•◍)
                //start display GRADE ABC(◍•ᴗ•◍)
                if (weekObject.marks_type.contains("GradeABC")){

                    if(!studentResult.containsKey("GradeABC")){
                        //no marks for this student but other students has mark
                        //or marked false
                        shareRsult += "Grade(A,B,C...) :  -- (0) ,"
                        weeklyAVG += 0
                    } else {
                        var result = studentResult.get("GradeABC")

                        if (result =="A"){
                            weeklyAVG += 100
                        } else if (result == "B"){
                            weeklyAVG += 80
                        } else if (result == "C"){
                            weeklyAVG += 70
                        } else if (result == "D"){
                            weeklyAVG += 60
                        } else if (result == "F"){
                            weeklyAVG += 0
                        }
                        shareRsult += "Grade(A,B,C...) :  ${result.toString()} (${weeklyAVG.toString()}) ,"
                    }

                }




                }
            //end display GRADE ABC(◍•ᴗ•◍)
            //calc & display weekly AVG
            weeklyAVG?.let{

               var wholeAVG = weeklyAVG.average().toString()
                //wholeAVG = "wholeAVG = %.2f".format(wholeAVG)
                ui.textAVGResult.text = wholeAVG.toString()
                //End there is score in the week****
                shareRsult += "Avarage :  ${wholeAVG.toString()}  ,"

            }




        }
        }
        //share out
        ui.btnSend.setOnClickListener {
            var sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareRsult.toString())
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }

        //share in
        if (intent?.action == Intent.ACTION_SEND && intent?.type != null)
        {
            if (intent?.type == "text/plain")
            {
                var sharedText = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""
                //do something with sharedText
                //ui.lblSharedText.text = "Recieved: $sharedText"
            }
        }

    }

    override fun onResume() {
       super.onResume()
        //connect firestore
        i = 0
/*        val db = Firebase.firestore
        val studentsCollection = db.collection("students")
        studentsCollection
            .get()
            .addOnSuccessListener { result ->
                items.clear() //this line clears the list, and prevents a bug where items would be duplicated upon rotation of screen
                Log.d(FIREBASE_TAG, "--- all students ---")
                for (document in result)
                {
                    Log.d(FIREBASE_TAG, document.toString())
                    val student = document.toObject<Student>()
                    student.id = document.id
                    Log.d(FIREBASE_TAG, student.toString())
                    items.add(student)
                }

            }*/
        ui.MyStudentRsultList.adapter?.notifyDataSetChanged()

    }

    inner class StudentListResultHolder(var ui: MyListItemResultBinding) :
            RecyclerView.ViewHolder(ui.root) {
    }

    inner class StudentListResultAdapter(private val students: MutableList<HashMap<String, String>>) :
            RecyclerView.Adapter<StudentListResultHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListResultHolder {
            val ui = MyListItemResultBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml

            return StudentListResultHolder(ui)
        }

        override fun onBindViewHolder(holder: StudentListResultHolder, position: Int) {
/*            val student = students[position]   //get the data at the requested position
            var studentName : String = student.given_name.toString() + " " + student.family_name.toString()
            holder.ui.txtStudentName.text = studentName
            holder.ui.txtStudentID.text = student.studentID.toString()

            holder.ui.root.setOnClickListener {
                var i = Intent(holder.ui.root.context, StudentResult::class.java)
                i.putExtra(STUDENT_INDEX, position)
                startActivity(i)
            }*/

            var studentObject = items[studentID!!]
            var size = studentObject.grades.size
            var studentResult = studentObject.grades[i]

            //display week number
            var weekNum = i + 1
            var weekName = "week${weekNum}"
            holder.ui.textWeekNum.text = weekName
            i++

            //no score flag
            var flgNomark = false
            var flgNoWkmark = false
            var weekObject = Weeks()
            if (weeksitems.size == 0 ){
                flgNomark = false
            } else {
                flgNomark = true
                var lpweeksize = weeksitems.size -1
                for (x in 0..lpweeksize){
                    if(weeksitems[x].week_name == weekName){
                        weekObject = weeksitems[x]
                        flgNoWkmark = true
                        break
                    } else {

                    }
                }

                //calc total AVG


            }


            //no all mark
            if(!flgNomark){
                ui.textAGV.text = ""
                ui.textAVGResult.text = "No score "
            }else {
                //score
                var maxScore = studentResult["score_MAX"]
                holder.ui.textScoreMax.text = "/${maxScore}"

                //no weekly score
                if(!flgNoWkmark){
                    //setting display for no weekly score
                    holder.ui.textWeekNum.text = "${weekName}  -NO MARKED-"
                    holder.ui.tableResult.visibility = View.GONE
                    holder.ui.textWkAvg.text = ""
                    holder.ui.textWkAGVScore.text = ""
                } else {
                    //holder.ui.textAttendanceTitle.text = studentResult.get("Attendance_title")
                    // weekly score start!!
                        var weeklyResult : Array<Int> = arrayOf()
                    //start display Attendance (◍•ᴗ•◍)
                    if (!weekObject.marks_type.contains("Attendance")){
                        holder.ui.RowAttendance.visibility = View.GONE
                    } else {
                        if(studentResult.containsKey("Attendance")) {
                            var result = studentResult.get("Attendance")
                            if (result == null || result == "false") {
                                //no marks for this student but other students has mark
                                //or marked false
                                holder.ui.checkBoxAttendance.isChecked = false
                                weeklyResult += 0
                            } else {
                                holder.ui.checkBoxAttendance.isChecked = true
                                weeklyResult += 100
                            }
                        } else {
                            weeklyResult += 0
                        }
                    }
                    //end  display Attendance (◍•ᴗ•◍)
                    //start display Check box (◍•ᴗ•◍)
                    if (!weekObject.marks_type.contains("CheckPoints")){
                        holder.ui.RowCheckbox.visibility = View.GONE
                    } else {
                        //holder.ui.textCheckBoxTitle.text = studentResult.get("CheckPoints_title")
                        var check_score = 0
                        if(studentResult.containsKey("CheckBox_MAX")){

                        //get weekly score
                        var max_checkbox = studentResult.get("CheckBox_MAX").toString().toInt()


                        var result = studentResult.get("CheckBox1")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.checkBox1.isChecked = false

                        } else {
                            holder.ui.checkBox1.isChecked = true
                            check_score = 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox2")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.checkBox2.isChecked = false

                        } else {
                            holder.ui.checkBox2.isChecked = true
                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox3")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.checkBox3.isChecked = false

                        } else {
                            holder.ui.checkBox3.isChecked = true
                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox4")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.checkBox4.isChecked = false

                        } else {
                            holder.ui.checkBox4.isChecked = true
                            check_score += 100/max_checkbox
                        }
                        result = studentResult.get("CheckBox5")
                        if(result == null || result == "false"){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.checkBox5.isChecked = false

                        } else {
                            holder.ui.checkBox5.isChecked = true
                            check_score += 100/max_checkbox
                        }

                        //add on result list
                        weeklyResult += check_score

                        if (max_checkbox == 1){

                            holder.ui.checkBox2.visibility = View.GONE
                            holder.ui.checkBox3.visibility = View.GONE
                            holder.ui.checkBox4.visibility = View.GONE
                            holder.ui.checkBox5.visibility = View.GONE

                        } else if (max_checkbox == 2){

                            holder.ui.checkBox2.visibility = View.VISIBLE
                            holder.ui.checkBox3.visibility = View.GONE
                            holder.ui.checkBox4.visibility = View.GONE
                            holder.ui.checkBox5.visibility = View.GONE
                        } else if (max_checkbox == 3) {

                            holder.ui.checkBox2.visibility = View.VISIBLE
                            holder.ui.checkBox3.visibility = View.VISIBLE
                            holder.ui.checkBox4.visibility = View.GONE
                            holder.ui.checkBox5.visibility = View.GONE
                        }else if (max_checkbox == 4) {

                            holder.ui.checkBox2.visibility = View.VISIBLE
                            holder.ui.checkBox3.visibility = View.VISIBLE
                            holder.ui.checkBox4.visibility = View.VISIBLE
                            holder.ui.checkBox5.visibility = View.GONE
                        }else if (max_checkbox == 5) {

                            holder.ui.checkBox2.visibility = View.VISIBLE
                            holder.ui.checkBox3.visibility = View.VISIBLE
                            holder.ui.checkBox4.visibility = View.VISIBLE
                            holder.ui.checkBox5.visibility = View.VISIBLE
                        }
                        } else{
                            // if no marks but other student has
                            weeklyResult += 0
                        }
                    }

                    //end display Check box (◍•ᴗ•◍)

                    //start display Score(◍•ᴗ•◍)
                    if (!weekObject.marks_type.contains("Score")){
                        holder.ui.RowScore.visibility = View.GONE
                    } else {

                        if(!studentResult.containsKey("score")){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.textScore.text = "0"
                            holder.ui.textScoreMax.text =""
                            weeklyResult += 0
                        } else {
                            holder.ui.textScoreTitle.text = studentResult.get("score_title")
                            var max_score = studentResult.get("score_MAX").toString().toInt()
                            var result = studentResult.get("score")
                            holder.ui.textScore.text = result

                            var scoreforAVG = (result.toString().toDouble()/max_score.toDouble())*100
                            weeklyResult += scoreforAVG.toInt()
                            //holder.ui.textScoreper.text = scoreforAVG.toString()
                        }

                    }
                    //end display Score(◍•ᴗ•◍)

                    //start display GRADE HD(◍•ᴗ•◍)
                    if (!weekObject.marks_type.contains("GradeHD")){
                        holder.ui.RowGradeHD.visibility = View.GONE
                    } else {

                        if(!studentResult.containsKey("GradeHD")){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.textGradeHDresult.text = "--"
                            weeklyResult += 0
                        } else {
                            //holder.ui.textGradeHD.text = studentResult.get("GradeHD_mark_title")
                            var result = studentResult.get("GradeHD")
                            holder.ui.textGradeHDresult.text = result
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
                        holder.ui.RowGradeABC.visibility = View.GONE
                    } else {
                        if(!studentResult.containsKey("GradeABC")){
                            //no marks for this student but other students has mark
                            //or marked false
                            holder.ui.textGradeABCresult.text = "--"
                            weeklyResult += 0
                        } else {
                            //holder.ui.textGradeABC.text = studentResult.get("GradeABC_mark_title")
                            var result = studentResult.get("GradeABC")
                            holder.ui.textGradeABCresult.text = result
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
                        holder.ui.textWkAGVScore.text = weeklyAVG


                    }


                //End there is score in the week****
                }

            }
        }

        override fun getItemCount(): Int {
            return students.size
        }
    }
}

