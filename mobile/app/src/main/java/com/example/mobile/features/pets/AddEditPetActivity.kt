package com.example.mobile.features.pets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobile.R
import com.example.mobile.network.RetrofitClient
import com.example.mobile.util.PreferencesManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditPetActivity : AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private lateinit var prefsManager: PreferencesManager

    private lateinit var tvTitle: TextView
    private lateinit var etName: EditText
    private lateinit var etBreed: EditText
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var etSpecialNeeds: EditText
    private lateinit var etPhotoUrl: EditText
    private lateinit var spSpecies: Spinner
    private lateinit var spVaccination: Spinner
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private var petId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_pet)

        prefsManager = PreferencesManager(this)

        tvTitle = findViewById(R.id.tvPetFormTitle)
        etName = findViewById(R.id.etPetName)
        etBreed = findViewById(R.id.etPetBreed)
        etAge = findViewById(R.id.etPetAge)
        etWeight = findViewById(R.id.etPetWeight)
        etSpecialNeeds = findViewById(R.id.etPetSpecialNeeds)
        etPhotoUrl = findViewById(R.id.etPetPhotoUrl)
        spSpecies = findViewById(R.id.spPetSpecies)
        spVaccination = findViewById(R.id.spPetVaccination)
        btnSave = findViewById(R.id.btnSavePet)
        btnCancel = findViewById(R.id.btnCancelPet)

        setupSpinners()
        bindExistingPet()

        btnSave.setOnClickListener { savePet() }
        btnCancel.setOnClickListener { finish() }
    }

    private fun setupSpinners() {
        val speciesOptions = listOf("DOG", "CAT", "BIRD", "RABBIT", "OTHER")
        val speciesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, speciesOptions)
        speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSpecies.adapter = speciesAdapter

        val vaccinationOptions = listOf("UP_TO_DATE", "OVERDUE", "NOT_VACCINATED")
        val vaccinationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, vaccinationOptions)
        vaccinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spVaccination.adapter = vaccinationAdapter
    }

    private fun bindExistingPet() {
        petId = intent.getStringExtra(EXTRA_PET_ID)
        if (petId == null) {
            tvTitle.text = "Add Pet"
            return
        }

        tvTitle.text = "Edit Pet"
        etName.setText(intent.getStringExtra(EXTRA_PET_NAME) ?: "")
        etBreed.setText(intent.getStringExtra(EXTRA_PET_BREED) ?: "")
        etAge.setText(intent.getStringExtra(EXTRA_PET_AGE) ?: "")
        etWeight.setText(intent.getStringExtra(EXTRA_PET_WEIGHT) ?: "")
        etSpecialNeeds.setText(intent.getStringExtra(EXTRA_PET_SPECIAL) ?: "")
        etPhotoUrl.setText(intent.getStringExtra(EXTRA_PET_PHOTO) ?: "")

        val species = intent.getStringExtra(EXTRA_PET_SPECIES) ?: "OTHER"
        val speciesIndex = (spSpecies.adapter as ArrayAdapter<String>).getPosition(species)
        spSpecies.setSelection(if (speciesIndex >= 0) speciesIndex else 0)

        val vaccination = intent.getStringExtra(EXTRA_PET_VAX) ?: "NOT_VACCINATED"
        val vaxIndex = (spVaccination.adapter as ArrayAdapter<String>).getPosition(vaccination)
        spVaccination.setSelection(if (vaxIndex >= 0) vaxIndex else 0)
    }

    private fun savePet() {
        val token = prefsManager.getAuthToken() ?: return

        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Pet name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = PetUpsertRequest(
            name = name,
            breed = etBreed.text.toString().trim().ifEmpty { null },
            age = etAge.text.toString().trim().toIntOrNull(),
            weight = etWeight.text.toString().trim().toDoubleOrNull(),
            species = spSpecies.selectedItem.toString(),
            specialNeeds = etSpecialNeeds.text.toString().trim().ifEmpty { null },
            vaccinationStatus = spVaccination.selectedItem.toString(),
            photoUrl = etPhotoUrl.text.toString().trim().ifEmpty { null }
        )

        btnSave.isEnabled = false

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    if (petId == null) {
                        RetrofitClient.apiService.createPet("Bearer $token", request)
                    } else {
                        RetrofitClient.apiService.updatePet("Bearer $token", petId!!, request)
                    }
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@AddEditPetActivity, "Pet saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddEditPetActivity, "Failed to save pet", Toast.LENGTH_SHORT).show()
                    btnSave.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddEditPetActivity, "Error saving pet", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    companion object {
        private const val EXTRA_PET_ID = "extra_pet_id"
        private const val EXTRA_PET_NAME = "extra_pet_name"
        private const val EXTRA_PET_BREED = "extra_pet_breed"
        private const val EXTRA_PET_AGE = "extra_pet_age"
        private const val EXTRA_PET_WEIGHT = "extra_pet_weight"
        private const val EXTRA_PET_SPECIES = "extra_pet_species"
        private const val EXTRA_PET_SPECIAL = "extra_pet_special"
        private const val EXTRA_PET_VAX = "extra_pet_vax"
        private const val EXTRA_PET_PHOTO = "extra_pet_photo"

        fun newIntent(context: Context, pet: Pet?): Intent {
            return Intent(context, AddEditPetActivity::class.java).apply {
                if (pet != null) {
                    putExtra(EXTRA_PET_ID, pet.petId)
                    putExtra(EXTRA_PET_NAME, pet.name)
                    putExtra(EXTRA_PET_BREED, pet.breed)
                    putExtra(EXTRA_PET_AGE, pet.age?.toString())
                    putExtra(EXTRA_PET_WEIGHT, pet.weight?.toString())
                    putExtra(EXTRA_PET_SPECIES, pet.species)
                    putExtra(EXTRA_PET_SPECIAL, pet.specialNeeds)
                    putExtra(EXTRA_PET_VAX, pet.vaccinationStatus)
                    putExtra(EXTRA_PET_PHOTO, pet.photoUrl)
                }
            }
        }
    }
}
