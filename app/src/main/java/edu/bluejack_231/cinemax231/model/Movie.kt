package edu.bluejack_231.cinemax231.model

import android.content.Intent

class Movie (
    var name: String,
    var duration: Int,
    var genre: String,
    var language: String,
    var description: String,
    var price: Int,
    var image: String,
    var schedules: String,
    var time: String
        )    {
    constructor(
        name: String,
        duration: Int,
        genre: String,
        language: String,
        description: String,
        price: Int,
        image: String
    ) : this(name, duration, genre, language, description, price, image, "", "")
    constructor(
        name: String,
        duration: Int,
        genre: String,
        language: String,
        description: String,
        price: Int,
        image: String,
        schedules: String
    ) : this(name, duration, genre, language, description, price, image, schedules, "")

    companion object {
        fun createFromIntent(intent: Intent) : Movie {
            val name = intent.getSerializableExtra("movie_name") as String
            val duration = intent.getSerializableExtra("movie_duration") as Int
            val genre = intent.getSerializableExtra("movie_genre") as String
            val language = intent.getSerializableExtra("movie_language") as String
            val desc = intent.getSerializableExtra("movie_description") as String
            val price = intent.getSerializableExtra("movie_price") as Int
            val image = intent.getSerializableExtra("movie_image") as String
            val schedules = intent.getSerializableExtra("movie_schedules") as String

            return Movie(name, duration, genre, language, desc, price, image, schedules, "")
        }

        fun createEmpty(): Movie {
            return Movie("", 0, "", "", "", 0, "", "", "")
        }

        fun createFromDocument(data: Map<String, Any>): Movie {
            val name = data["name"].toString()
            val duration = Integer.parseInt(data["duration"].toString())
            val genre = data["genre"].toString()
            val language = data["language"].toString()
            val description = data["description"].toString()
            val price = Integer.parseInt(data["price"].toString())
            val image = data["image"].toString()
            val time = data["time"].toString()

            return Movie(
                name = name,
                duration = duration,
                genre = genre,
                language = language,
                description = description,
                price = price,
                image = image,
                schedules = "",
                time = time
            )
        }
    }
    public fun passIntent(intent: Intent) {
        intent.putExtra("movie_name", this.name)
        intent.putExtra("movie_duration", this.duration)
        intent.putExtra("movie_genre", this.genre)
        intent.putExtra("movie_language", this.language)
        intent.putExtra("movie_description", this.description)
        intent.putExtra("movie_price", this.price)
        intent.putExtra("movie_image", this.image)
        intent.putExtra("movie_schedules", this.schedules)
    }
}