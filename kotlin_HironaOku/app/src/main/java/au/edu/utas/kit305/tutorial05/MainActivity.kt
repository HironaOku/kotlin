package au.edu.utas.kit305.tutorial05

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import au.edu.utas.kit305.tutorial05.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
//const val MOVIE_INDEX = "Movie_Index"
//val items = mutableListOf<Movie>()
//const val FIREBASE_TAG = "FirebaseLogging"

class MainActivity : AppCompatActivity()
{
    private lateinit var ui : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        //When click mark button, open mark activity
        ui.markBtn.setOnClickListener{
            val i = Intent(this, Mark::class.java)
            startActivity(i)
        }

        //When click summary button, open WeeklySummary activity
        ui.summaryBtn.setOnClickListener{
            val i = Intent(this, WeeklySummary::class.java)
            startActivity(i)
        }

        //When click studentListBtn button, open StudentList activity
        ui.studentListBtn.setOnClickListener{
            val i = Intent(this, StudentList::class.java)
            startActivity(i)
        }

/*        //get db connection
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
                        Log.d(FIREBASE_TAG, weeks.toString())

                        weeksitems.add(weeks)

                    }


                }
                .addOnFailureListener {
                    Log.d(FIREBASE_TAG, "Error getting week list")
                }*/



/*        ui.lblMovieCount.text = "${items.size} Movies"
        ui.myList.adapter = MovieAdapter(movies = items)

        //vertical list
        ui.myList.layoutManager = LinearLayoutManager(this)*/

/*        //get db connection
        val db = Firebase.firestore
        val moviesCollection = db.collection("movies")

        val lotr = Movie(
                title = "Singing in the rain",
                year = 1980,
                duration = 9001F
        )*/

/*        moviesCollection
                .add(lotr)
                .addOnSuccessListener {
                    Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                    lotr.id = it.id
                }
                .addOnFailureListener {
                    Log.e(FIREBASE_TAG, "Error writing document", it)
                }*/


//get all movies
/*        ui.lblMovieCount.text = "Loading..."
        var movieCount = 0
        moviesCollection
                .get()
                .addOnSuccessListener { result ->
                    Log.d(FIREBASE_TAG, "--- all movies ---")
                    for (document in result)
                    {
                        //Log.d(FIREBASE_TAG, document.toString())
                        val movie = document.toObject<Movie>()
                        movie.id = document.id
                        Log.d(FIREBASE_TAG, movie.toString())

                        items.add(movie)
                        (ui.myList.adapter as MovieAdapter).notifyDataSetChanged()

                        movieCount++
                        ui.lblMovieCount.text = "${movieCount.toString()} Movie(s)"
                    }
                }*/




    }
//onResume is called when the activity is brought back to the foreground.
/*    override fun onResume() {
        super.onResume()

        ui.myList.adapter?.notifyDataSetChanged()
    }*/
   /* inner class MovieHolder(var ui: MyListItemBinding) : RecyclerView.ViewHolder(ui.root) {}*/

/*    inner class MovieAdapter(private val movies: MutableList<Movie>) : RecyclerView.Adapter<MovieHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainActivity.MovieHolder {
            val ui = MyListItemBinding.inflate(layoutInflater, parent, false)   //inflate a new row from the my_list_item.xml
            return MovieHolder(ui)                                                            //wrap it in a ViewHolder
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        override fun onBindViewHolder(holder: MainActivity.MovieHolder, position: Int) {
            val movie = movies[position]   //get the data at the requested position
            holder.ui.txtName.text = movie.title
            holder.ui.txtYear.text = movie.year.toString()

            holder.ui.root.setOnClickListener {
                var i = Intent(holder.ui.root.context, MovieDetails::class.java)
                i.putExtra(MOVIE_INDEX, position)
                startActivity(i)
            }
        }
    }*/

}

