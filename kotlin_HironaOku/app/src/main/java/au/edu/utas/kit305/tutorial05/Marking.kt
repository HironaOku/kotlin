package au.edu.utas.kit305.tutorial05

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityMarkingBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemMarkingBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import au.edu.utas.kit305.tutorial05.Student as Student1


//const val STUDENT_INDEX = "Student_Index"
//val items = mutableListOf<Student1>()
//const val FIREBASE_TAG = "FirebaseLogging"
class Marking : AppCompatActivity() {
    private lateinit var ui: ActivityMarkingBinding
    var selectedweek : String? = null
    var checkedStyle : String? = null
    var optionValue : String? = null
    var score: String? = null
    var layerPosition = -1
    var i = 0
    var flgr = false

    var checkBox1: String? = null
    var checkBox2: String? = null
    var checkBox3: String? = null
    var checkBox4: String? = null
    var checkBox5: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ui = ActivityMarkingBinding.inflate(layoutInflater)
        setContentView(ui.root)



        var SelectedStyle: Array<String>? = intent.getStringArrayExtra(SELECTED_MARK_STYLE.toString())
        selectedweek = SelectedStyle?.get(0).toString()
        checkedStyle = SelectedStyle?.get(1).toString()
        optionValue = SelectedStyle?.get(2).toString()

        ui.MyStudentMarkingList.adapter = StudentMarkingListAdapter(students = items)

        //vertical list
        ui.MyStudentMarkingList.layoutManager = LinearLayoutManager(this)

        //get db connection
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
                    val student = document.toObject<Student1>()
                    student.id = document.id
                    Log.d(FIREBASE_TAG, student.toString())

                    items.add(student)

                    (ui.MyStudentMarkingList.adapter as StudentMarkingListAdapter).notifyDataSetChanged()
                }


            }
            .addOnFailureListener {
                Log.d(FIREBASE_TAG, "Error getting students list")
            }

       /* val weeksCollection = db.collection("weeks")
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

                        (ui.MyStudentMarkingList.adapter as StudentMarkingListAdapter).notifyDataSetChanged()
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }*/



        //set value on display
        ui.textMarkingTitle.text = "Marking for Week ${selectedweek}"
        ui.editMarkingTitle.setText(checkedStyle)

        ui.buttonDONE.setOnClickListener{
            finish()
        }

    }
    

    private fun gradeMarkingScheme(): Array<String> {
        var sppinerGradeitems: Array<String>
        if(checkedStyle == "GradeHD") {
            sppinerGradeitems = arrayOf(
                    "-",
                    "HD+",
                    "HD",
                    "DN",
                    "CR",
                    "PP",
                    "NN")
        }else {

            sppinerGradeitems = arrayOf(
                    "-",
                    "A",
                    "B",
                    "C",
                    "D",
                    "F")
        }

        return sppinerGradeitems
    }

    //set number of checkbox(s)
    private fun CheckBoxMarkingScheme(): Int {
        var numCheckBox = 0
        numCheckBox = if (checkedStyle == "Attendance"){
            1
        } else {
            optionValue?.toInt() ?: 1
        }

        return numCheckBox
    }

    //onResume is called when the activity is brought back to the foreground.
    override fun onResume() {
        super.onResume()

        ui.MyStudentMarkingList.adapter?.notifyDataSetChanged()
    }

    inner class StudentMaringListHolder(var ui: MyListItemMarkingBinding) :
        RecyclerView.ViewHolder(ui.root)

    inner class StudentMarkingListAdapter(private val students: MutableList<Student1>) :
        RecyclerView.Adapter<StudentMaringListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentMaringListHolder {
            val ui = MyListItemMarkingBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return StudentMaringListHolder(ui)
        }

        override fun onBindViewHolder(holder: StudentMaringListHolder, position: Int) {
            val student = students[position]   //get the data at the requested position
            var studentName : String = student.given_name.toString() + " " + student.family_name.toString()
            holder.ui.txtStudentNameM.text = studentName
            holder.ui.txtStudentIDM.text = student.studentID.toString()
            var studentID = student.studentID
            val keyPair = mapOf(studentID to position)


            //set marking schema
            holder.ui.LinearCheckBox.visibility = View.GONE
            holder.ui.LinearScore.visibility = View.GONE
            holder.ui.LinearGrade.visibility = View.GONE

            if (checkedStyle == "Attendance" || checkedStyle == "CheckPoints" ) {
                holder.ui.LinearCheckBox.visibility = View.VISIBLE

                holder.ui.checkBox2.visibility = View.GONE
                holder.ui.checkBox3.visibility = View.GONE
                holder.ui.checkBox4.visibility = View.GONE
                holder.ui.checkBox5.visibility = View.GONE

                var numCheckBox = CheckBoxMarkingScheme()

                // I know this is wired was thinking for loop
                when (numCheckBox) {
                    2 -> {
                        holder.ui.checkBox2.visibility = View.VISIBLE
                    }
                    3 -> {
                        holder.ui.checkBox2.visibility = View.VISIBLE
                        holder.ui.checkBox3.visibility = View.VISIBLE
                    }
                    4 -> {
                        holder.ui.checkBox2.visibility = View.VISIBLE
                        holder.ui.checkBox3.visibility = View.VISIBLE
                        holder.ui.checkBox4.visibility = View.VISIBLE
                    }
                    5 -> {
                        holder.ui.checkBox2.visibility = View.VISIBLE
                        holder.ui.checkBox3.visibility = View.VISIBLE
                        holder.ui.checkBox4.visibility = View.VISIBLE
                        holder.ui.checkBox5.visibility = View.VISIBLE
                    }
                }



            } else if (checkedStyle == "Score"){
                holder.ui.LinearScore.visibility = View.VISIBLE
                holder.ui.textScoreMax.text = "/${optionValue}"

            } else if (checkedStyle == "GradeHD"  || checkedStyle == "GradeABC"){
                holder.ui.LinearGrade.visibility = View.VISIBLE
                var sppinerGradeitems = gradeMarkingScheme()
                //from  SpinnerSample
                val adapter = ArrayAdapter(applicationContext,
                        R.layout.simple_spinner_item, sppinerGradeitems)
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

                holder.ui.spinnerGrade.adapter = adapter
            }


            //spiner changing
            holder.ui.LinearGrade.setOnClickListener {
                 layerPosition = position
            }
            holder.ui.spinnerGrade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val id = id

                    layerPosition = -1
                    layerPosition = keyPair[studentID]!!
                    if(id == 0.toLong() && !flgr){

                    } else {
                        flgr = true
                        //val student = studentID
                        val selectedItem = parent.getItemAtPosition(position).toString()
                        //var selectedStudentID = parent.get
                        Log.d("TAG", selectedItem)
                        val score = holder.ui.spinnerGrade.selectedItem.toString()
                        addGrade(selectedItem, i)
                        val studentID = holder.ui.txtStudentIDM.text.toString()

                    }
                    i++
                    Log.d("TAG", i.toString())
                } // to close the onItemSelected

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

            //when checkbox is clicked
            holder.ui.checkBox1.setOnClickListener {
                if (holder.ui.checkBox1.isChecked) {
                    checkBox1 = "true"
                } else {
                    checkBox1 = "false"
                }
                if (holder.ui.checkBox2.isChecked) {
                    checkBox2 = "true"
                } else {
                    checkBox2 = "false"
                }
                if (holder.ui.checkBox3.isChecked) {
                    checkBox3 = "true"
                } else {
                    checkBox3 = "false"
                }
                if (holder.ui.checkBox4.isChecked) {
                    checkBox4 = "true"
                } else {
                    checkBox4 = "false"
                }
                if (holder.ui.checkBox5.isChecked) {
                    checkBox5 = "true"
                } else {
                    checkBox5 = "false"
                }
                addGrade(checkBox1!!, position)
            }
            holder.ui.checkBox2.setOnClickListener {

                if (holder.ui.checkBox1.isChecked) {
                    checkBox1 = "true"
                } else {
                    checkBox1 = "false"
                }
                if (holder.ui.checkBox2.isChecked) {
                    checkBox2 = "true"
                } else {
                    checkBox2 = "false"
                }
                if (holder.ui.checkBox3.isChecked) {
                    checkBox3 = "true"
                } else {
                    checkBox3 = "false"
                }
                if (holder.ui.checkBox4.isChecked) {
                    checkBox4 = "true"
                } else {
                    checkBox4 = "false"
                }
                if (holder.ui.checkBox5.isChecked) {
                    checkBox5 = "true"
                } else {
                    checkBox5 = "false"
                }
                addGrade(checkBox2!!, position)


            }
            holder.ui.checkBox3.setOnClickListener {

                if (holder.ui.checkBox1.isChecked) {
                    checkBox1 = "true"
                } else {
                    checkBox1 = "false"
                }
                if (holder.ui.checkBox2.isChecked) {
                    checkBox2 = "true"
                } else {
                    checkBox2 = "false"
                }
                if (holder.ui.checkBox3.isChecked) {
                    checkBox3 = "true"
                } else {
                    checkBox3 = "false"
                }
                if (holder.ui.checkBox4.isChecked) {
                    checkBox4 = "true"
                } else {
                    checkBox4 = "false"
                }
                if (holder.ui.checkBox5.isChecked) {
                    checkBox5 = "true"
                } else {
                    checkBox5 = "false"
                }
                addGrade(checkBox3!!, position)

            }
            holder.ui.checkBox4.setOnClickListener {
                //Toast.makeText(applicationContext, "checkBox4 is clicked!", Toast.LENGTH_SHORT).show()
                if (holder.ui.checkBox1.isChecked) {
                    checkBox1 = "true"
                } else {
                    checkBox1 = "false"
                }
                if (holder.ui.checkBox2.isChecked) {
                    checkBox2 = "true"
                } else {
                    checkBox2 = "false"
                }
                if (holder.ui.checkBox3.isChecked) {
                    checkBox3 = "true"
                } else {
                    checkBox3 = "false"
                }
                if (holder.ui.checkBox4.isChecked) {
                    checkBox4 = "true"
                } else {
                    checkBox4 = "false"
                }
                if (holder.ui.checkBox5.isChecked) {
                    checkBox5 = "true"
                } else {
                    checkBox5 = "false"
                }
                addGrade(checkBox4!!, position)

            }
            holder.ui.checkBox5.setOnClickListener {
                //Toast.makeText(applicationContext, "checkBox5 is clicked!", Toast.LENGTH_SHORT).show()
                if (holder.ui.checkBox1.isChecked) {
                    checkBox1 = "true"
                } else {
                    checkBox1 = "false"
                }
                if (holder.ui.checkBox2.isChecked) {
                    checkBox2 = "true"
                } else {
                    checkBox2 = "false"
                }
                if (holder.ui.checkBox3.isChecked) {
                    checkBox3 = "true"
                } else {
                    checkBox3 = "false"
                }
                if (holder.ui.checkBox4.isChecked) {
                    checkBox4 = "true"
                } else {
                    checkBox4 = "false"
                }
                if (holder.ui.checkBox5.isChecked) {
                    checkBox5 = "true"
                } else {
                    checkBox5 = "false"
                }
                addGrade(checkBox5!!, position)

            }

            //from consultation
            holder.ui.editScore.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                    var score = holder.ui.editScore.text.toString()
                    //check entered value
                    when {

                        score.toInt() > optionValue.toString().toInt() -> {
                            holder.ui.textWarning.text = "Woops. Max:${optionValue}"
                        }
                        score.toInt() < 0 -> {
                            holder.ui.textWarning.text = "Rally!? give more"
                        }
                        else -> {
                            holder.ui.textWarning.text = ""

                        }
                    }

                }

                override fun afterTextChanged(s: Editable?) {

                }
            })

            //ref: android developper https://developer.android.com/reference/android/view/inputmethod/EditorInfo
            holder.ui.editScore.setOnEditorActionListener { view, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    var score = holder.ui.editScore.text.toString()
                    addGrade(score, position)

                }
                if (actionId == EditorInfo.IME_ACTION_NEXT) { //issue: it stop to focusing next edittext....
                    var score = holder.ui.editScore.text.toString()
                    addGrade(score, position)

                }
                return@setOnEditorActionListener true
            }

        }

        private fun addGrade(score: String, i: Int) {

            //connect firestore
            val db = Firebase.firestore
            val studentsCollection = db.collection("students")
            val weeksCollection = db.collection("weeks")
            // for (i in items) {
            //get entered values
            val markValue = MarkType(
                    week = selectedweek.toString().toInt(),
                    mark_title = ui.editMarkingTitle.text.toString(),
                    mark_type = checkedStyle.toString(),
                    score = score
            )


            if(checkedStyle != "GradeHD" && checkedStyle != "GradeABC" ){
                layerPosition = i //position bcz spinner in recycle view is notty
            }
                var studentObject = items[layerPosition]
                var weekObjectSize = weeksitems.size



            layerPosition.let {
                //create grade schema for wk12
                val weekNum = (selectedweek.toString().toInt()-1)
                var gradesNum = studentObject.grades.size
                var createWeekSchema : HashMap<String, String> = hashMapOf()
                var flgNewStudent = true
                flgNewStudent = gradesNum != 0
                if(gradesNum == 0) {
                    var i = 0
                    do {
                            var weekSchema = hashMapOf(
                                    "week" to gradesNum.toString()
                            )
                            studentObject.grades.add(weekSchema)

                            i++

                    } while (i < 12)
                }
                var result2 = studentObject.grades.get(weekNum)
                //result2.idscore = ...
                //result2.score_title = ...
                //var week : HashMap<String,String> = hashMapOf()    from consltation
                if (checkedStyle == "GradeHD"){

                    result2.set("week",markValue.week.toString())
                    result2.set("GradeHD_mark_title",markValue.mark_title.toString())
                    result2.set("GradeHD_mark_type",markValue.mark_type.toString())
                    result2.set("GradeHD",markValue.score.toString())

                } else if (checkedStyle == "GradeABC"){

                    result2.set("week",markValue.week.toString())
                    result2.set("GradeABC_mark_title",markValue.mark_title.toString())
                    result2.set("GradeABC_mark_type",markValue.mark_type.toString())
                    result2.set("GradeABC",score)

                } else if (checkedStyle == "Attendance"){

                    result2.set("week",markValue.week.toString())
                    result2.set("Attendance_title",markValue.mark_title.toString())
                    result2.set("Attendance_mark_type",markValue.mark_type.toString())
                    result2.set("Attendance",markValue.score.toString())

                } else if (checkedStyle == "CheckPoints"){

                    result2.set("week",markValue.week.toString())
                    result2.set("CheckPoints_title",markValue.mark_title.toString())
                    result2.set("CheckPoints_mark_type",markValue.mark_type.toString())
                    result2.set("CheckBox1",checkBox1.toString())
                    result2.set("CheckBox2",checkBox2.toString())
                    result2.set("CheckBox3",checkBox3.toString())
                    result2.set("CheckBox4",checkBox4.toString())
                    result2.set("CheckBox5",checkBox5.toString())
                    result2.set("CheckBox_MAX",optionValue.toString())

                } else if  (checkedStyle == "Score"){

                    result2.set("week",markValue.week.toString())
                    result2.set("score_title",markValue.mark_title.toString())
                    result2.set("score_mark_type",markValue.mark_type.toString())
                    result2.set("score",score.toString())
                    result2.set("score_MAX",optionValue.toString())
                }

                /*var weekName = "week${selectedweek.toString().toInt()}"

                    var weekObject : Weeks = Weeks()
                    var weekItemsize = weeksitems.size
                    //get week marktype item in selected weeks

                    for (x in 0..weekItemsize){
                        var wkobje = weeksitems[x].week_name
                        if (wkobje == weekName){
                            weekObject = weeksitems[x]
                            if(!weekObject.marks_type.contains(checkedStyle)){
                                weekObject.marks_type.add(checkedStyle!!)
                            }
                            weekObject.week_name = selectedweek
                            break
                        } else {
                            weekObject.marks_type = mutableListOf(checkedStyle!!)
                            weekObject.week_name = selectedweek
                        }
                    }

                    //result3.set(checkedStyle)
                    //var result4 = weekObject.get(weekNum)
                    //var result3 = weekObject.marks_type.get(weekNum)

                    val data = weekObject

                    if (data != null) {
                        db.collection("weeks").document("${weekName.toString()}")
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.d(FIREBASE_TAG, "Successfully updated weeks checkstyle as ${checkedStyle}")
                                    Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { exception ->
                                    Log.d(FIREBASE_TAG, "Error getting documents: ", exception)
                                }
                    }*/
                //weekObject.WEEKS.add(checkedStyle)
                //weekObject.weeks.set(checkedStyle.toString())
                //weekObject.set(weekNum, result)
                //result to studentObject.grades[weekNum]
                //(weekNum, result) // from cnsul
                /*  weeksObject.weeks.set(weeks, result)
                            .add(data)*/
                /*             weeksCollection.document(weeks)
                            .update("mark_type", FieldValue.arrayUnion(checkedStyle))
                            .addOnSuccessListener {
                                Log.d(FIREBASE_TAG, "Successfully updated weeks checkstyle as ${checkedStyle}")
                                Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()

                            }
                            .addOnFailureListener { exception ->
                                Log.d(FIREBASE_TAG, "Error getting documents: ", exception)
                            }*/
                /* val lotr = Weeks(
                            //weeks = weeks
                    )*/


                /*               checkedStyle?.let { it1 ->
                        weeksCollection.document("${weekName}")
                                .set(it1, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.d(FIREBASE_TAG, "Successfully updated week schema ${weekName}:checkedStyle")
                                    Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { exception ->
                                    Log.d(FIREBASE_TAG, "Error getting documents: ", exception)
                                }
                    }*/
/*                if (flgNewStudent == false) {
                    var i = 0
                    do {
                        if (i != weekNum) {
                            var weekSchema = hashMapOf(
                                    "week" to gradesNum.toString()
                            )
                            studentObject.grades[i]=weekSchema

                            i++
                        }
                    } while (i < 12)
                }*/
                studentObject.grades[weekNum]=result2
                studentsCollection.document(studentObject.id!!)
                        .set(studentObject, SetOptions.merge())

                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Successfully updated student ${studentObject.id}")
                            //Log.d("TAggggggggggggggggggggggggggggg", "${weekNum}")
                            Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT).show()


                        }
                        .addOnFailureListener { exception ->
                            Log.d(FIREBASE_TAG, "Error getting documents: ", exception)
                        }
                // }
            }
        }

        override fun getItemCount(): Int {
            return students.size
        }
    }
}


