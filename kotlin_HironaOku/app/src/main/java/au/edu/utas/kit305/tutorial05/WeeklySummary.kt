package au.edu.utas.kit305.tutorial05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityWeeklySummaryBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemWeeklyBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
var totalAVG:Array<Int> = arrayOf()
class WeeklySummary : AppCompatActivity() {
    private lateinit var ui: ActivityWeeklySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityWeeklySummaryBinding.inflate(layoutInflater)
        setContentView(ui.root)



        //vertical list
        ui.MyWeeklyList.layoutManager = LinearLayoutManager(this)

        //get db connection
        val db = Firebase.firestore
        val studentsCollection = db.collection("students")

        //ui.NumStudents.text = "Loading..."
        var studentCount = 0

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

                        (ui.MyWeeklyList.adapter as WeeklySummaryAdapter).notifyDataSetChanged()
                        studentCount++
                    }

                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting students list")
                }

        ui.MyWeeklyList.adapter = WeeklySummaryAdapter(students = items)
       // var studentObject = items[studentID!!]
        //ui.MyWeeklyList.adapter = MyWeeklyListAdapter(items = items)


        if(studentCount == 0){
           //todo --------
        }
        //val db = Firebase.firestore
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
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }


    }//END onCreate
    override fun onResume() {
        super.onResume()
        ui.MyWeeklyList.adapter?.notifyDataSetChanged()

    }

    inner class WeeklySummaryHolder(var ui: MyListItemWeeklyBinding) :
            RecyclerView.ViewHolder(ui.root) {
    }

    inner class WeeklySummaryAdapter(private val students: MutableList<Student>) :
            RecyclerView.Adapter<WeeklySummaryHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklySummaryHolder {
            val ui = MyListItemWeeklyBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml

            return WeeklySummaryHolder(ui)
        }

        override fun onBindViewHolder(holder: WeeklySummaryHolder, position: Int) {
            var studentNum = students.size

            //display week name oon subtitle
            var weekNum = position + 1
            var weekName = "week${weekNum}"
            holder.ui.textWeekNum2.text = weekName

            //START no score flag
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


            } //END no score flg
//calc total AVG
            if(flgNoWkmark == true){


                if (items.size != 0 ){

                    var weeklyAVG : Array<Int> = arrayOf()


                    var AttendanseAVG : Array<Int> = arrayOf()
                    var CheckBOXAVG: Array<Int> = arrayOf()
                    var ScoreAVG : Array<Int> = arrayOf()
                    var HDAVG : Array<Int> = arrayOf()
                    var ABCAVG : Array<Int> = arrayOf()

                    var studentObject = Student()
                    var studentNUMloop = items.size -1

                    //loop nubmer of student!!!!!
                    for (x in 0..studentNUMloop) {
                        var studentObject = items[x!!] //todo
                        if(studentObject.grades.size != 0) {


                            var studentResult = studentObject.grades[position]

                            //start display Attendance (◍•ᴗ•◍)
                            if (weekObject.marks_type.contains("Attendance")) {
                                if (studentResult.containsKey("Attendance")) {
                                    var result = studentResult.get("Attendance")
                                    if (result == null || result == "false") {
                                        //no marks for this student but other students has mark
                                        //or marked false
                                        AttendanseAVG += 0
                                        weeklyAVG += 0
                                        totalAVG += 0
                                    } else {
                                        AttendanseAVG += 100
                                        weeklyAVG += 100
                                        totalAVG += 100
                                    }
                                } else {
                                    AttendanseAVG += 0
                                    weeklyAVG += 0
                                    totalAVG += 0
                                }
                            } else {
                                holder.ui.RowAttendance2.visibility = View.GONE
                            }
                            //end  display Attendance (◍•ᴗ•◍)
                        } else {
                            flgNoWkmark == false
                            break

                        }
                    }//for END student number


                    //calc AttendanseAVG
                    var AttendanseAV = AttendanseAVG.average().toString()
                    holder.ui.textAttendanceAVG.text= AttendanseAV


                    //loop nubmer of student!!!!!
                    for (x in 0..studentNUMloop) {
                        var studentObject = items[x!!] //todo
                        var studentResult = studentObject.grades[position]
                        //start calc Check box (◍•ᴗ•◍)
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

                                //add on result list
                                weeklyAVG += check_score
                                totalAVG += check_score
                                CheckBOXAVG += check_score


                            } else{
                                // if no marks but other student has
                                weeklyAVG += 0
                                totalAVG += 0
                                CheckBOXAVG += 0
                            }
                        }else {

                                holder.ui.RowCheckbox2.visibility = View.GONE

                        }

                        //end display Check box (◍•ᴗ•◍)


                    }//end of loop
                    //calc AttendanseAVG
                    var CheckBOXAV = CheckBOXAVG.average().toString()
                    holder.ui.textCHKAVG.text= CheckBOXAV

                    //loop nubmer of student!!!!!
                    for (x in 0..studentNUMloop) {
                        var studentObject = items[x!!] //todo
                        var studentResult = studentObject.grades[position]
                    //start calc Score(◍•ᴗ•◍)
                        if (weekObject.marks_type.contains("Score")){

                            if(!studentResult.containsKey("score")){
                                //no marks for this student but other students has mark
                                //or marked false

                                weeklyAVG += 0
                                totalAVG += 0
                                ScoreAVG += 0
                            } else {
                                var max_score = studentResult.get("score_MAX").toString().toInt()
                                var result = studentResult.get("score")

                                var scoreforAVG = (result.toString().toDouble()/max_score.toDouble())*100
                                weeklyAVG += scoreforAVG.toInt()
                                totalAVG += scoreforAVG.toInt()
                                ScoreAVG += scoreforAVG.toInt()
                                //holder.ui.textScoreper.text = scoreforAVG.toString()
                            }

                        }else {

                            holder.ui.RowScore2.visibility = View.GONE

                        }
                        //end display Score(◍•ᴗ•◍)


                    }//end of loop
                    //calc AttendanseAVG
                    var ScoreAV = ScoreAVG.average().toString()
                    holder.ui.textScoreAVG.text= ScoreAV

                    //loop nubmer of student!!!!!
                    for (x in 0..studentNUMloop) {
                        var studentObject = items[x!!] //todo
                        var studentResult = studentObject.grades[position]

//start display GRADE HD(◍•ᴗ•◍)
                        if (weekObject.marks_type.contains("GradeHD")){

                            if(!studentResult.containsKey("GradeHD")){
                                //no marks for this student but other students has mark
                                //or marked false

                                weeklyAVG += 0
                                HDAVG += 0
                                totalAVG += 0

                            } else {
                                var result = studentResult.get("GradeHD")

                                if (result =="HD+"){
                                    weeklyAVG += 100
                                    HDAVG += 100
                                    totalAVG += 100
                                } else if (result == "HD"){
                                    weeklyAVG += 80
                                    HDAVG += 80
                                    totalAVG += 80
                                } else if (result == "DN"){
                                    weeklyAVG += 70
                                    HDAVG += 70
                                    totalAVG += 70
                                } else if (result == "CR"){
                                    weeklyAVG += 60
                                    HDAVG += 60
                                    totalAVG += 60
                                } else if (result == "PP"){
                                    weeklyAVG += 50
                                    HDAVG += 50
                                    totalAVG += 50
                                } else if (result == "NN"){
                                    weeklyAVG += 0
                                    HDAVG += 0
                                    totalAVG += 0
                                }

                            }

                        }else {

                            holder.ui.RowGradeHD2.visibility = View.GONE

                        }
                        //end display GRADE HD(◍•ᴗ•◍)

                    }//end of loop
                    //calc AttendanseAVG
                    var HDAV = HDAVG.average().toString()
                    holder.ui.textHDAVG.text= HDAV

                    //loop nubmer of student!!!!!
                    for (x in 0..studentNUMloop) {
                        var studentObject = items[x!!] //todo
                        var studentResult = studentObject.grades[position]
//start display GRADE ABC(◍•ᴗ•◍)
                        if (weekObject.marks_type.contains("GradeABC")){

                            if(!studentResult.containsKey("GradeABC")){
                                //no marks for this student but other students has mark
                                //or marked false

                                weeklyAVG += 0
                                ABCAVG += 0
                                totalAVG += 0
                            } else {
                                var result = studentResult.get("GradeABC")

                                if (result =="A"){
                                    weeklyAVG += 100
                                    ABCAVG += 100
                                    totalAVG += 100
                                } else if (result == "B"){
                                    weeklyAVG += 80
                                    ABCAVG += 80
                                    totalAVG += 80
                                } else if (result == "C"){
                                    weeklyAVG += 70
                                    ABCAVG += 70
                                    totalAVG += 70
                                } else if (result == "D"){
                                    weeklyAVG += 60
                                    ABCAVG += 60
                                    totalAVG += 60
                                } else if (result == "F"){
                                    weeklyAVG += 0
                                    ABCAVG += 0
                                    totalAVG += 0
                                }
                            }

                        }else {

                            holder.ui.RowGradeABC2.visibility = View.GONE

                        }

                        //end display GRADE ABC(◍•ᴗ•◍)


                    }//end of loop
                    //calc AttendanseAVG
                    var ABCAV = ABCAVG.average().toString()
                    holder.ui.textABCAVG.text= ABCAV

                    //weekly AVG
                    var weeklyAV = weeklyAVG.average().toString()
                    holder.ui.textWkAGVscore.text= weeklyAV
                    /*








                    //calc & display weekly AVG
                    weeklyAVG?.let{

                        var wholeAVG = weeklyAVG.average().toString()
                        //wholeAVG = "wholeAVG = %.2f".format(wholeAVG)
                        //ui.textAVGResult.text = wholeAVG.toString()

                        //End there is score in the week****

                    }*/




                }
            }


    }

        override fun getItemCount(): Int {
            return 12 //12 week
        }
    }
}
