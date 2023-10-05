package au.edu.utas.kit305.tutorial05

data class Student(
    var id: String? = null,
    var studentID: Int? = null,
    var family_name: String? = null,
    var given_name: String? = null,
    var photo: String? = null,

    var grades: MutableList<HashMap<String, String>> = mutableListOf<HashMap<String, String>>()

)


    //from Bonus - Storing Lists in Firebase Firestore
    //https://mylo.utas.edu.au/d2l/le/content/434620/viewContent/3958697/View
/*    //some useful operations, once you have an instance of a student (from the database)
    student.grades?.add(100)
    student.grades?.set(2,      60) //set week 3's grade (if using 0-indexing)
//                  ^index, ^value*/
