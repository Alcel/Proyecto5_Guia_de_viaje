package com.example.proyecto5_guadeviaje

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.drawToBitmap
import com.example.proyecto5_guadeviaje.databinding.ActivityAddUpdateBinding

class AddUpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddUpdateBinding
    var nombre=""
    var asignatura=""
    var email=""
    var id: Int? = null
    lateinit var imagen: ImageView
    lateinit var conexion: DataBase
    var editar = false
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var  values: ContentValues

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        conexion = DataBase(this)
        imagen = binding.imageView2
        cogerDatos()
        setListeners()
        binding.button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.eleccion)

            builder.setPositiveButton(R.string.camara) { dialog, which ->
                dispatchTakePictureIntent()
            }
            builder.setNeutralButton(R.string.almacenamiento) { dialog, which ->

                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }
            builder.show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100) {
            var imageUri = data?.data
            if (imageUri != null) {
                imagen.setImageURI(imageUri)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 102) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            if (imageBitmap != null) {

                binding.imageView2.setImageBitmap(imageBitmap)
                //  }

            }
        }
    }

    private fun cogerDatos() {
        val datos = intent.extras
        if(datos!=null){
            editar= true

            val usuario = datos.getSerializable("USUARIO") as Usuarios
            id=usuario.id
            binding.etNombre.setText(usuario.nombre)
            binding.etAsignatura.setText(usuario.asig)
            binding.etEmail.setText(usuario.email)
            binding.imageView2.setImageBitmap(DbBitmapUtility.getImage(usuario.imagen))
        }
    }
    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }
        binding.btnCrear.setOnClickListener {
            crearRegistro()
        }
    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, 102)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }
    private fun crearRegistro() {
        var imagenConv = DbBitmapUtility.getBytes(imagen.drawToBitmap())
        nombre=binding.etNombre.text.toString().trim()
        email=binding.etEmail.text.toString().trim()
        asignatura=binding.etAsignatura.text.toString().trim()
        if(nombre.length<3){
            binding.etNombre.setError("El campo nombre debe tener al menos 3 caracteres")
            return
        }
        /*
        if(email.length<6){
            binding.etEmail.setError("El campo email debe tener al menos 6 caracteres")
            binding.etEmail.requestFocus()
            return
        }
        //el email no esta duplicado

        if(conexion.existeEmail(email, id)){
            binding.etEmail.setError("El email YA está registrado.")
            binding.etEmail.requestFocus()
            return
        }*/
        if(!editar){
            val usuario=Usuarios(1, nombre,asignatura,email,imagenConv)
            if(conexion.crear(usuario)>-1){
                finish()
            }
            else{
                Toast.makeText(this, "NO se pudo guardar el registro!!!", Toast.LENGTH_SHORT).show()
            }
        }else{
            val usuario=Usuarios(id, nombre,asignatura, email,imagenConv)
            if(conexion.update(usuario)>-1){
                finish()
            }
            else{
                Toast.makeText(this, "NO se pudo editar el registro!!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}