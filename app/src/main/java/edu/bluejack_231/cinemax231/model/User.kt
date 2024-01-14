package edu.bluejack_231.cinemax231.model
class User(
    var username: String,
    var phone: String,
    var password: String,
    var profile: String
) {
    companion object {
        public fun createFromDoc(data: Map<String, Any>): User {
            return User(
                data["username"].toString(),
                data["phone"].toString(),
                data["password"].toString(),
                data.getOrDefault("profile", "").toString()
            )
        }
    }
}