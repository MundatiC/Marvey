package com.example.marvey

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvey.Extensions.toast
import com.example.marvey.Models.CartModel
import com.example.marvey.databinding.FragmentCartpageBinding
import com.example.marvey.rvadapters.CartAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment(R.layout.fragment_cartpage), CartAdapter.OnLongClickRemove {

    private lateinit var binding: FragmentCartpageBinding
    private lateinit var cartList: ArrayList<CartModel>
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: CartAdapter
    private var subTotalPrice = 0
    private var totalPrice = 0

    private var orderDatabaseReference = Firebase.firestore.collection("orders")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartpageBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        binding.cartActualToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }


        val layoutManager = LinearLayoutManager(context)


        cartList = ArrayList()

        retrieveCartItems()



        adapter = CartAdapter(requireContext(),cartList ,this)
        binding.rvCartItems.adapter = adapter
        binding.rvCartItems.layoutManager = layoutManager






        binding.btnCartCheckout.setOnClickListener {

            requireActivity().toast("Order worth ${totalPrice} has been received")
            cartList.clear()
            binding.tvLastSubTotalprice.text ="0"
            binding.tvLastTotalPrice.text ="Min 1 product is Required"
            binding.tvLastTotalPrice.setTextColor(Color.RED)
            // TODO: remove the data of the Products from the fireStore after checkout or insert a boolean isDelivered
            val query = orderDatabaseReference.get()
            query.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        document.reference.delete()
                    }
                }
            }
            adapter.notifyDataSetChanged()

            navigateToOrderActivity(totalPrice.toString())



        }


    }




    @SuppressLint("SuspiciousIndentation")
     fun retrieveCartItems() {

        orderDatabaseReference
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (item in querySnapshot) {
                    val cartProduct = item.toObject<CartModel>()


                        cartList.add(cartProduct)
                        subTotalPrice += cartProduct.price!!.toInt() * cartProduct.quantity!!.toInt()
                        totalPrice += cartProduct.price!!.toInt() * cartProduct.quantity!!.toInt()
                        binding.tvLastSubTotalprice.text = subTotalPrice.toString()
                        binding.tvLastTotalPrice.text = totalPrice.toString()
                        binding.tvLastSubTotalItems.text = "SubTotal Items(${cartList.size})"
                        adapter.notifyDataSetChanged()



                }

            }
            .addOnFailureListener{
                requireActivity().toast(it.localizedMessage!!)
            }


    }

    override fun onLongRemove(item: CartModel , position:Int) {


        orderDatabaseReference
            .whereEqualTo("uid",item.uid)
            .whereEqualTo("pid",item.pid)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (item in querySnapshot){
                    val cartProduct = item.toObject<CartModel>()
                    orderDatabaseReference.document(item.id).delete()
                    cartList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    subTotalPrice -= cartProduct.price!!.toInt() * cartProduct.quantity!!.toInt()
                    totalPrice -= cartProduct.price!!.toInt() * cartProduct.quantity!!.toInt()
                    binding.tvLastSubTotalprice.text = subTotalPrice.toString()
                    binding.tvLastTotalPrice.text = totalPrice.toString()
                    binding.tvLastSubTotalItems.text = "SubTotal Items(${cartList.size})"



                    requireActivity().toast("Removed Successfully!!!")
                }


            }
            .addOnFailureListener {
                requireActivity().toast("Failed to remove")
            }








    }
    fun navigateToOrderActivity(totalPrice: String)
    {
        val intent = Intent(requireActivity(), Order::class.java)
        intent.putExtra("totalPrice", totalPrice)
        startActivity(intent)
    }



}