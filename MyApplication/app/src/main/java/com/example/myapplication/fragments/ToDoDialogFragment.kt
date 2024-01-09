package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentToDoDialogBinding
import com.example.myapplication.utils.model.ToDoData
import com.google.android.material.textfield.TextInputEditText


class ToDoDialogFragment : DialogFragment() {

    private lateinit var binding:FragmentToDoDialogBinding
    private var listener : OnDialogNextBtnClickListener? = null
    private var toDoData: ToDoData? = null


    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String,description: String) =
            ToDoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                    putString("description", description)
                }
            }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentToDoDialogBinding.inflate(inflater , container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){

            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString(),
                arguments?.getString("description").toString()
            )
            binding.todoEt.setText(toDoData?.task)
            binding.todoEtDescription.setText(toDoData?.taskDescription)
        }


        binding.todoClose.setOnClickListener {
            dismiss()
        }

        binding.todoNextBtn.setOnClickListener {

            val todoTask = binding.todoEt.text.toString()
            val todoDescription = binding.todoEtDescription.text.toString()
            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener?.saveTask(todoTask, todoDescription, binding.todoEt,binding.todoEtDescription)
                }else{
                    toDoData!!.task = todoTask
                    toDoData!!.taskDescription = todoDescription
                    listener?.updateTask(toDoData!!, binding.todoEt,binding.todoEtDescription)
                }

            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun saveTask(todoTask:String, todoDescription: String, todoEdit:TextInputEditText,todoEditDescription:TextInputEditText)
        fun updateTask(toDoData: ToDoData, todoEdit:TextInputEditText,todoEditDescription:TextInputEditText)
    }

}