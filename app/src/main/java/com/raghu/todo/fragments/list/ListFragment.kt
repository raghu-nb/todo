package com.raghu.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.raghu.todo.R
import com.raghu.todo.data.ToDoViewModel
import com.raghu.todo.data.models.ToDoData
import com.raghu.todo.databinding.FragmentListBinding
import com.raghu.todo.fragments.list.adapter.ListAdapter
import com.raghu.todo.fragments.viewmodel.SharedViewModel
import com.raghu.todo.utils.ObserveOnce
import com.raghu.todo.utils.hideKeyBoard


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val adapter: ListAdapter by lazy{ ListAdapter() }
    private val mToDoViewModel : ToDoViewModel by viewModels()
    private val mSharedViewModel : SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Data binding
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner=this
        binding.mSharedViewModel =mSharedViewModel

        setupReyclerView()

        // Observer livedata
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
            binding.recyclerview.scheduleLayoutAnimation()
        })

        binding.listlayout.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_updateFragment)
        }

        setHasOptionsMenu(true)
        hideKeyBoard(requireActivity())

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.list_fragment_menu,menu)

        val search:MenuItem =menu.findItem(R.id.menu_search)
        val searchView:SearchView? = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_deleteall -> confirmDeleteAll()
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriroity.observe(viewLifecycleOwner,{ adapter?.setData(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriroity.observe(viewLifecycleOwner,{ adapter?.setData(it) })
        }

        if(item.itemId == R.id.menu_deleteall){
            confirmDeleteAll()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupReyclerView(){
        val recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        onSwipeToDelete(recyclerView)
    }

    private fun confirmDeleteAll() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mToDoViewModel.deleteAll()
            Toast.makeText(context,"Deleted successfully everything", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_,_-> }
        builder.setTitle("Delete All")
        builder.setMessage("Are you sure you want to Delete All?")
        builder.create().show()
    }

    private fun onSwipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                Toast.makeText(requireContext(),"Successfully Removed",Toast.LENGTH_SHORT).show()
                restoreDeletedItem(viewHolder.itemView,itemToDelete)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun restoreDeletedItem(view:View,deletedItem:ToDoData){
        val snackbar = Snackbar.make(view,"Deleted '${deletedItem.title}'",Snackbar.LENGTH_LONG)

        snackbar.setAction("Undo"){
            mToDoViewModel.insertData(deletedItem)
        }
        snackbar.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchThroughDatabase(newText)
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        var searchQuery = "%$query%"

        mToDoViewModel.searchDatabse(searchQuery).ObserveOnce(viewLifecycleOwner, Observer {
            adapter?.setData(it)
        })

    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}