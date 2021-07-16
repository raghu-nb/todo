package com.raghu.todo.fragments.add

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.raghu.todo.R
import com.raghu.todo.data.ToDoViewModel
import com.raghu.todo.data.models.Priority
import com.raghu.todo.data.models.ToDoData
import com.raghu.todo.databinding.FragmentAddBinding
import com.raghu.todo.fragments.viewmodel.SharedViewModel

class AddFragment : Fragment() {

    private var _binding:FragmentAddBinding? =null
    private val binding get()=_binding!!

    private val mToDOViewModel : ToDoViewModel by viewModels()
    private val mSharedViewModel : SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
      //  val view = inflater.inflate(R.layout.fragment_add, container, false)


        binding.prioritiesspinner.onItemSelectedListener = mSharedViewModel.listener
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.menu_done){
            insertDatatoDB()
        }
        return super.onOptionsItemSelected(item)
    }

    fun insertDatatoDB(){
        val mTitle = binding.titleet.text.toString()
        val mPriority = binding.prioritiesspinner.selectedItem.toString()
        val mDesc = binding.descriptionet.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle,mDesc)

        if(validation){
            val newData = ToDoData(0,mTitle,mSharedViewModel.parsePriority(mPriority),mDesc)

            mToDOViewModel.insertData(newData)
            Toast.makeText(context,"Successfully Added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(context,"Title or(/and) Desc Empty", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}