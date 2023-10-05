package au.edu.utas.kit305.tutorial05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.kit305.tutorial05.databinding.ActivityStudentListBinding
import au.edu.utas.kit305.tutorial05.databinding.MyListItemBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import au.edu.utas.kit305.tutorial05.Student as Student1

//const val STUDENT_INDEX = "Student_Index"
val items = mutableListOf<Student1>()
val weeksitems = mutableListOf<Weeks>()
//const val FIREBASE_TAG = "FirebaseLogging"

class StudentList : AppCompatActivity() {

    private lateinit var ui: ActivityStudentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(ui.root)

        ui.MyStudentList.adapter = StudentListAdapter(students = items)

        //vertical list
        ui.MyStudentList.layoutManager = LinearLayoutManager(this)

        //get db connection
        val db = Firebase.firestore
        val studentsCollection = db.collection("students")

        ui.NumStudents.text = "Loading..."
        var studentCount = 0

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

                        (ui.MyStudentList.adapter as StudentListAdapter).notifyDataSetChanged()
                        studentCount++
                        ui.NumStudents.text  = "${studentCount.toString()} Student(s)"
                    }

                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting students list")
                }
        if(studentCount == 0){
            ui.NumStudents.text = "No student on your class list"
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

                        //(ui.My.adapter as Marking.StudentMarkingListAdapter).notifyDataSetChanged()
                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }


        //when click the add student button move to add activity
        ui.AddStudentButton.setOnClickListener{

            val i = Intent(this, EditStudent::class.java)
            startActivity(i)
        }
    }
    //onResume is called when the activity is brought back to the foreground.
    override fun onResume() {
        super.onResume()

        ui.MyStudentList.adapter?.notifyDataSetChanged()
    }

    inner class StudentListHolder(var ui: MyListItemBinding) :
        RecyclerView.ViewHolder(ui.root) {
    }

    inner class StudentListAdapter(private val students: MutableList<Student1>) :
        RecyclerView.Adapter<StudentListHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return StudentListHolder(ui)
        }

        override fun onBindViewHolder(holder: StudentListHolder, position: Int) {
            val student = students[position]   //get the data at the requested position
            var studentName : String = student.given_name.toString() + " " + student.family_name.toString()
            holder.ui.txtStudentName.text = studentName
            holder.ui.txtStudentID.text = student.studentID.toString()

            holder.ui.root.setOnClickListener {
                var i = Intent(holder.ui.root.context, StudentResult::class.java)
                i.putExtra(STUDENT_INDEX, position)
                startActivity(i)
            }
        }

        override fun getItemCount(): Int {
            return students.size
        }
    }
}