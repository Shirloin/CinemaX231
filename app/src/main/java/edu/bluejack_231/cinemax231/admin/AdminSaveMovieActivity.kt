package edu.bluejack_231.cinemax231.admin

import ScheduleViewerAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.model.Movie
import edu.bluejack_231.cinemax231.utility.dialog.AddScheduleDialog
import edu.bluejack_231.cinemax231.utility.dialog.LoadingDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.model.AMovie
import edu.bluejack_231.cinemax231.model.Schedule
import edu.bluejack_231.cinemax231.utility.dialog.ErrorDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminSaveMovieActivity : AppCompatActivity() {
    var filePath: Uri? = null
    lateinit var moviePoster: ImageView
    lateinit var editMoviePosterButton: Button
    lateinit var movieTitle: TextInputEditText
    lateinit var movieDuration: TextInputEditText
    lateinit var censorRating: Spinner
    lateinit var genre: TextInputEditText
    lateinit var language: TextInputEditText
    lateinit var description: TextInputEditText
    lateinit var addScheduleButton: Button
    lateinit var saveButton: Button

    val loadingDialog = LoadingDialog(this)
    val errorDialog = ErrorDialog(this)
    val addScheduleDialog = AddScheduleDialog(this)

    lateinit var movie: AMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_save_movie)

        GlobalScope.launch(Dispatchers.Main) {
            loadingDialog.startLoadingDialog()
            init()
            componentInit()

            editMoviePosterButton.setOnClickListener {
                imagePicker()
            }
            saveButton.setOnClickListener {
                saveMovie()
            }
            addScheduleButton.setOnClickListener {
                addScheduleDialog.startDialog(movie.duration)
            }
            loadingDialog.stop()
        }

    }

    fun init() {
        moviePoster = findViewById<ImageView>(R.id.admin_save_movie_movieImage)
        editMoviePosterButton = findViewById<Button>(R.id.admin_save_movie_edit_picture_button)
        movieTitle = findViewById<TextInputEditText>(R.id.admin_save_movie_movieTitle)
        movieDuration = findViewById<TextInputEditText>(R.id.admin_save_movie_duration)
        censorRating = findViewById<Spinner>(R.id.admin_save_movie_censorRating)
        genre = findViewById<TextInputEditText>(R.id.admin_save_movie_genre)
        language = findViewById<TextInputEditText>(R.id.admin_save_movie_Language)
        description = findViewById<TextInputEditText>(R.id.admin_save_movie_Description)
        addScheduleButton = findViewById<Button>(R.id.admin_save_movie_addSchedule)
        saveButton = findViewById<Button>(R.id.admin_save_movie_save)

        censorRating.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCensorRating = parent.getItemIdAtPosition(id.toInt()).toString()
                movie.censorRating = selectedCensorRating
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Kosong
            }
        }
    }

    suspend fun componentInit() {
        val intent = getIntent()
        movie = AMovie.createFromIntent(intent)

        if(movie.image.isBlank()) {
            addScheduleButton.visibility = View.GONE
            ArrayAdapter.createFromResource(
                this,
                R.array.censor_ratings,
                android.R.layout.simple_spinner_item
            ).also {adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                censorRating.adapter = adapter
            }
            return
        }

        val db = Firebase.firestore
        val document = db.collection("movies").document(movie.name).get().await()
        val data = document.data!!

        val name = data["name"].toString()
        val duration = Integer.parseInt(data["duration"].toString())
        val genre = data["genre"].toString()
        val language = data["language"].toString()
        val description = data["description"].toString()
        val price = Integer.parseInt(data["price"].toString())
        val image = data["image"].toString()
        val censorRating = data["rating"]?.toString()?:""

        movie = AMovie(
            name = name,
            duration = duration,
            genre = genre,
            language = language,
            description = description,
            price = price,
            image = image,
            censorRating
        )
        setComponent(document)
    }

    suspend fun setComponent(document: DocumentSnapshot) {
        if(movie.image.isNotBlank()) {
            movieTitle.isEnabled = false
            Picasso.get().load(movie.image).into(moviePoster)
        }
        movieTitle.setText(movie.name)
        movieDuration.setText(movie.duration.toString())
        genre.setText(movie.genre)
        language.setText(movie.language)
        description.setText(movie.description)

        ArrayAdapter.createFromResource(
            this,
            R.array.censor_ratings,
            android.R.layout.simple_spinner_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            censorRating.adapter = adapter
        }

        val existingCensorRating = if(movie.censorRating.isNullOrBlank()) 0 else Integer.parseInt(movie.censorRating)
        val censorRatingsArray = resources.getStringArray(R.array.censor_ratings)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            censorRatingsArray
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        censorRating.adapter = adapter
        censorRating.setSelection(existingCensorRating)

        val db = Firebase.firestore
        val path = "movies/" + document.id + "/schedules"


        val snapshots = db.collection(path).get().await()
        val schedules = mutableListOf<Schedule>()

        for(documentSnapshot in snapshots.documents) {
            val timeList = ArrayList<String>()
            val date = documentSnapshot.id

            val snap = db.collection("$path/$date/time").get().await()
                for(docSnap in snap.documents) {
                    Log.d("Inside For Loop", docSnap.id)
                    timeList.add(docSnap.id)
                }
            Log.d("Dewbug", timeList.toString())
            schedules.add(Schedule(date, timeList))
        }

        val adp = ScheduleViewerAdapter(schedules)
        val scheduleContainer = findViewById<RecyclerView>(R.id.admin_save_movie_schedule_viewer)
        scheduleContainer.adapter = adp
    }

    fun saveMovie() {
        val db = Firebase.firestore

        val movieData = hashMapOf(
            "description" to description.text.toString(),
            "duration" to movieDuration.text.toString(),
            "genre" to genre.text.toString(),
            "image" to movie.image,
            "language" to language.text.toString(),
            "name" to movieTitle.text.toString(),
            "nowshowing" to true.toString(),
            "rating" to movie.censorRating,
            "price" to 50000.toString(),
        )

        if(!validate(movieData)) return

        if(filePath != null) {
            loadingDialog.startLoadingDialog()

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference

            val imageRef = storageRef.child(movieTitle.text.toString())

            imageRef.putFile(filePath!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    movieData["image"] = uri.toString()
                    db.collection("movies")
                        .document(movieTitle.text.toString())
                        .set(movieData)
                        .addOnSuccessListener {
                            loadingDialog.stop()
                            finish()
                        }
                }
            }
        } else {
            if(movie.image.isBlank()) {
                errorDialog.startDialog("Please upload movie poster");
                return;
            }

            loadingDialog.startLoadingDialog()

            db.collection("movies")
                .document(movieTitle.text.toString())
                .set(movieData)
                .addOnSuccessListener {
                    loadingDialog.stop()
                    finish()
                }
        }
    }

    fun validate(data:  HashMap<String, String>) : Boolean {
        // Validate title
        val title = data["name"].toString()
        if(title.isNullOrBlank()) {
            errorDialog.startDialog("Please input movie title")
            return false
        }

        if(title.length > 44) {
            errorDialog.startDialog("Movie Title Cannot be More than 44 Characters")
            return false
        }

        // Validate duration
        try {
            val duration = data["duration"].toString()
            if(
                duration.toString().startsWith("0") ||
                duration.isNullOrBlank()
                ) throw Exception()

            val iDuration = duration.toInt()
            if(iDuration < 45) throw Exception()
        } catch (e: Exception) {
            errorDialog.startDialog("Please enter a valid duration!")
            return false
        }

        // Validate genre
        val genre = data["genre"].toString()
        if(genre.isNullOrBlank()) {
            errorDialog.startDialog("Please input genre")
            return false
        }

        // Validate language
        val language = data["language"].toString()
        if(language.isNullOrBlank()) {
            errorDialog.startDialog("Please input language")
            return false
        }

        val description = data["description"].toString()
        if(description.isNullOrBlank()) {
            errorDialog.startDialog("Please input description")
            return false
        }

        return true
    }

    fun imagePicker() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200)
    }

    override fun onActivityResult(requestCode: Int,  resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK) {
            if(requestCode == 200) {
                val image = data?.getData()
                if(image != null) {
                    filePath = image
                    moviePoster.setImageURI(image)
                }
            }
        }
    }

}