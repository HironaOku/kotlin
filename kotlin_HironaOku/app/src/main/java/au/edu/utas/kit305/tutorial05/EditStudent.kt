package au.edu.utas.kit305.tutorial05

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import au.edu.utas.kit305.tutorial05.databinding.ActivityEditStudentBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val STUDENT_INDEX = "Student_Index"
//val items = mutableListOf<Student>()
const val FIREBASE_TAG = "FirebaseLogging"
const val REQUEST_IMAGE_CAPTURE = 1
class EditStudent : AppCompatActivity() {

    private lateinit var ui : ActivityEditStudentBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        ui = ActivityEditStudentBinding.inflate(layoutInflater)
        setContentView(ui.root)


        //camerraaaaaaaa
        ui.buttnCamera.setOnClickListener {
            requestToTakeAPicture()
        }
        //read and set selected student information
        val studentID = intent.getIntExtra(STUDENT_INDEX, -1)


        if (studentID != -1) {
            //set visibility for DELETE BUTTON
            ui.buttonDelete.visibility = View.VISIBLE
            //For Edit student, set student information
           var studentObject = items[studentID]
            ui.editFamilyName.setText(studentObject.family_name)
            ui.editGivenName.setText(studentObject.given_name)
            ui.editStudentID.setText(studentObject.studentID.toString())
        } else {
            //set visibility for DELETE BUTTON
            ui.buttonDelete.visibility = View.INVISIBLE
            //For Add student set title
            ui.EditStudentTitle.text = "Add student"
        }

        //When click the save button
        ui.buttonSave.setOnClickListener {

            if ( ui.editFamilyName.text == null) {
                ui.textFamilyName.text = "Required Family Name"
            } else if ( ui.editGivenName.text == null){
                ui.textGivenName.text = "Required Given Name"
            } else if ( ui.editStudentID.text == null){
                ui.textStudentID.text = "Required Student ID"
            } else {

                //get entered values
                val lotr = Student(
                    studentID = ui.editStudentID.text.toString().toInt(),
                    family_name = ui.editFamilyName.text.toString(),
                    given_name = ui.editGivenName.text.toString()
                )

               var student_id = ui.editStudentID.text.toString().toInt()
               var family_name = ui.editFamilyName.text.toString()
               var given_name = ui.editGivenName.text.toString()

                //connect firestore
                val db = Firebase.firestore
                val studentsCollection = db.collection("students")

                if (studentID != -1) {
                    //update student
                    var studentObject = items[studentID]
                    val data = hashMapOf("family_name" to family_name,
                            "given_name" to given_name,
                            "studentID" to student_id
                            )

                    studentsCollection.document(studentObject.id!!)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Successfully updated student ${studentObject?.id}")
                            //return to the Student information and result
                            //val i = Intent(this, StudentList::class.java)
                            //startActivity(i)
                            finish()
                        }
                } else{
                    //add student

                    studentsCollection
                        .add(lotr)

                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                            lotr.id = it.id
                            val i = Intent(this, StudentList::class.java)
                            startActivity(i)
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e(FIREBASE_TAG, "Error writing document", it)
                        }

                }

            }
        }

        ui.buttonDelete.setOnClickListener {
            //delete student
            val db = Firebase.firestore
            val studentsCollection = db.collection("students")
            var studentObject = items[studentID]
            studentsCollection.document(studentObject.id!!)
                .delete()
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Successfully delete student ${studentObject?.id}")
                    //return to the list
                    val i = Intent(this, StudentList::class.java)
                    startActivity(i)
                    finish()
/*            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", { dialog, which ->

                })
                .setNegativeButton("Cancel", { dialog, which ->
                   finish()
                })
                .show()*/
                }
        }


    }
    //step 4
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestToTakeAPicture()
    {
        requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_IMAGE_CAPTURE
        )
    }
    //step 5
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode)
        {
            REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted.
                    takeAPicture()
                } else {
                    Toast.makeText(this, "Cannot access camera, permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    //step 6
    private fun takeAPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //try {
        val photoFile: File = createImageFile()!!
        val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "au.edu.utas.kit305.tutorial05",
                photoFile
        )
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        //} catch (e: Exception) {}

    }
    //step 6 part 2
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //step 7
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            setPic(ui.imageView2)
        }
    }
    //step 7 pt2
    private fun setPic(imageView: ImageView) {
        // Get the dimensions of the View
        val targetW: Int = imageView.measuredWidth
        val targetH: Int = imageView.measuredHeight

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true

            BitmapFactory.decodeFile(currentPhotoPath, this)

            val photoW: Int = outWidth
            val photoH: Int = outHeight

            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            imageView.setImageBitmap(bitmap)
        }
    }
}