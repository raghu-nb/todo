package com.raghu.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.renderscript.RenderScript
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.raghu.todo.R
import com.raghu.todo.data.ToDoViewModel
import com.raghu.todo.data.models.Priority
import com.raghu.todo.data.models.ToDoData
import com.raghu.todo.databinding.FragmentUpdateBinding
import com.raghu.todo.fragments.viewmodel.SharedViewModel

class UpdateFragment : Fragment() {

    val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModel:SharedViewModel by viewModels()
    private val mToDoViewmodel:ToDoViewModel by viewModels()

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentUpdateBinding.inflate(inflater,container,false)
        binding.args=args
        binding.lifecycleOwner=this

        setHasOptionsMenu(true)

        binding.currentPrioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.update_fragment_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemDelete()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateItem(){
        var title = binding.currentTitleEt.text.toString()
        var desc = binding.currentDescriptionEt.text.toString()
        var getPriority = binding.currentPrioritiesSpinner.selectedItem.toString()

        val validation = mSharedViewModel.verifyDataFromUser(title,desc)

        if(validation){
            val updatedItem = ToDoData(args.current.id,title,mSharedViewModel.parsePriority(getPriority),desc)
            mToDoViewmodel.updateData(updatedItem)
            Toast.makeText(context,"Updated successfully",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }else{
            Toast.makeText(context,"Title or(/and) Description Empty",Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmItemDelete() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewmodel.deleteData(args.current)
            Toast.makeText(context,"Deleted successfully",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete '${args.current.title}'")
        builder.setMessage("Are you sure you want to remove ${args.current.title}?")
        builder.create().show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}