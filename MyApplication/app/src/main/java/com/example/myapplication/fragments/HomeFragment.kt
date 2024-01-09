package com.example.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.utils.InternetUtil
import com.example.myapplication.utils.TaskDbHelper
import com.example.myapplication.utils.adapter.TaskAdapter
import com.example.myapplication.utils.model.ToDoData
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() ,ToDoDialogFragment.OnDialogNextBtnClickListener,
    TaskAdapter.TaskAdapterInterface, NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var internetUtil: InternetUtil
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>
    private lateinit var dbHelper: TaskDbHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        dbHelper = TaskDbHelper(requireContext())
        internetUtil = InternetUtil()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        //get data from firebase
        getTaskFromFirebase()


        binding.addTaskBtn.setOnClickListener {

            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
            )

        }
    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(
                            it,
                            taskSnapshot.child("task").value.toString(),
                            taskSnapshot.child("taskDescription").value.toString()
                        ) }

                    if (todoTask != null) {
                        toDoItemList.add(todoTask)
                    }

                }
                Log.d(TAG, "onDataChange: $toDoItemList")
                taskAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database("https://todo-kotlin-e26c0-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("Tasks")
            .child(authId)

        toDoItemList = mutableListOf()  // Move the initialization here

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }

    override fun saveTask(todoTask:String, todoDescription: String, todoEdit:TextInputEditText,todoEditDescription:TextInputEditText){
        dbHelper.addTask(todoTask, todoDescription);
        val todoData = ToDoData(database.push().key.toString(), todoTask, todoDescription)
    database
            .push().setValue(todoData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null
                    todoEditDescription.text = null

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText,todoEditDescription: TextInputEditText){
        dbHelper.updateTask(toDoData)
        database.child(toDoData.taskId).setValue(toDoData).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                todoEdit.text = null
                todoEditDescription.text = null
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        dbHelper.deleteTask(toDoData)
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task,toDoData.taskDescription)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                logoutUser()
            }
            // Add other cases for additional menu items if needed

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun logoutUser() {
        // Log out the user using FirebaseAuth
        FirebaseAuth.getInstance().signOut()

        // TODO: You may want to navigate the user to the login screen or perform other actions after logout

        // Example: If you are using Navigation component, navigate to the login fragment

    }
}