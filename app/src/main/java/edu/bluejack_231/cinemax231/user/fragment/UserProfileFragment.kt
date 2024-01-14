package edu.bluejack_231.cinemax231.user.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.bluejack_231.cinemax231.R
import edu.bluejack_231.cinemax231.auth.LoginActivity
import edu.bluejack_231.cinemax231.model.User
import edu.bluejack_231.cinemax231.utility.dialog.ChangePasswordDialog
import edu.bluejack_231.cinemax231.utility.dialog.ErrorDialog
import edu.bluejack_231.cinemax231.utility.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var usernameField: TextInputEditText
    lateinit var phoneField: TextInputEditText
    lateinit var imageView: ImageView

    lateinit var usernameButton: Button
    lateinit var passwordButton: Button
    lateinit var phoneButton: Button
    lateinit var imageButton: Button
    lateinit var logoutButton: Button

    lateinit var user: User
    lateinit var userID: String

    private lateinit var errorDialog: ErrorDialog
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var changePasswordDialog: ChangePasswordDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


        GlobalScope.launch(Dispatchers.Main) {
            loadingDialog.startLoadingDialog()
            fetch()
            loadingDialog.stop()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        usernameField = view.findViewById(R.id.user_profile_username)
        imageView = view.findViewById(R.id.user_profile_profile)
        phoneField = view.findViewById(R.id.user_profile_phone)

        usernameButton = view.findViewById(R.id.user_profile_change_username)
        passwordButton = view.findViewById(R.id.user_profile_change_password)
        imageButton = view.findViewById(R.id.user_profile_change_profile)
        phoneButton = view.findViewById(R.id.user_profile_change_phone)
        logoutButton = view.findViewById(R.id.user_profile_logout)

        usernameButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                loadingDialog.startLoadingDialog()
                val newName = usernameField.text.toString()

                val db = Firebase.firestore
                if(db.collection("users").document(newName).get().await().exists()) {
                    errorDialog.startDialog("Username already exists!")
                    usernameField.setText(user.username)
                    loadingDialog.stop()
                    return@launch
                }
                db.document("users/${userID}").update(mapOf(
                    "username" to usernameField.text.toString()
                )).await()

                val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("username", newName).apply()

                loadingDialog.stop()
            }
        }

        imageButton.setOnClickListener {
            imagePicker()
        }

        passwordButton.setOnClickListener {
            changePasswordDialog.startDialog(user)
        }

        phoneButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                loadingDialog.startLoadingDialog()
                val newPhone = phoneField.text.toString()
                val db = Firebase.firestore
                db.document("users/${userID}").update(mapOf(
                    "phone" to newPhone
                )).await()

                loadingDialog.stop()
            }
        }

        logoutButton.setOnClickListener {
            logOut()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(requireActivity())
        changePasswordDialog = ChangePasswordDialog(requireActivity())
        errorDialog = ErrorDialog(requireActivity())
    }

    private suspend fun fetch() {
        val sharedPreferences = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "").toString()

        val db = Firebase.firestore
        val document = db.collection("users").whereEqualTo("username", username).get().await()
        userID = document.documents[0].id
        user = User.createFromDoc(document.documents[0].data!!)


        usernameField.setText(user.username)
        phoneField.setText(user.phone)

        if(!user.profile.isNullOrBlank()) {
            Picasso.get().load(user.profile).into(imageView)
        }

    }

    fun imagePicker() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200)
    }

    override fun onActivityResult(requestCode: Int,  resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        loadingDialog.startLoadingDialog()
        if(resultCode == AppCompatActivity.RESULT_OK) {
            if(requestCode == 200) {
                val image = data?.getData()
                if(image != null) {
                    val filePath = image
                    imageView.setImageURI(image)

                    GlobalScope.launch(Dispatchers.Main) {
                        val db = Firebase.firestore
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference

                        val imageRef = storageRef.child(userID)

                        imageRef.putFile(filePath!!).await()
                        val downloadUrl = imageRef.downloadUrl.await()

                        db.collection("users").document(userID).update(
                            mapOf(
                                "profile" to downloadUrl
                            )
                        ).await()
                        loadingDialog.stop()

                    }
                }
            }
        }
    }

    fun logOut() {
        requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE).edit().remove("User").apply()
        requireActivity().startActivity(Intent(activity, LoginActivity::class.java))
        requireActivity().finish()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}