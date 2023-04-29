package com.example.marvey

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Order : AppCompatActivity() {


        private lateinit var priceTextView: TextView
        private lateinit var addressEditText: EditText
        private lateinit var submitBtn: Button
        private var price: String? = null
    private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_order)

            priceTextView = findViewById(R.id.priceTextView)
            addressEditText = findViewById(R.id.addressEditText)
            submitBtn = findViewById(R.id.submitBtn)

            price = intent.getStringExtra("totalPrice")
            priceTextView.text = "Total Price: $price"
            auth = FirebaseAuth.getInstance()

            submitBtn.setOnClickListener {
                submitOrder()
            }
            
        }

        private fun submitOrder() {
            val address = addressEditText.text.toString()

            val order = hashMapOf(
                "uid" to auth.currentUser!!.uid,
                "price" to price,
                "address" to address
            )

            val database = Firebase.firestore
            database.collection("orders1")
                .add(order)
                .addOnSuccessListener {
                    Toast.makeText(this, "Order submitted", Toast.LENGTH_SHORT).show()
                    addressEditText.text.clear()
                    val intent = Intent(this, Pay::class.java)
                    intent.putExtra("totalPrice", price)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to submit order", Toast.LENGTH_SHORT).show()
                }
        }
    }

