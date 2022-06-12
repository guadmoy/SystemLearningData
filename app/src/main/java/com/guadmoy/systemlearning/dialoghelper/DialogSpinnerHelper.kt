package com.guadmoy.systemlearning.dialoghelper

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.utils.UneverHelper
import java.util.ArrayList

class DialogSpinnerHelper {
    fun showSpinnerDialog(context: Context,list: ArrayList<String>,tvSelection:TextView){
        val builder = AlertDialog.Builder(context)
        val dialog =builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner,null)

        val adapter = RcViewDialogSpinnerAdapter(tvSelection,dialog)

        val rcView = rootView.findViewById<RecyclerView>(R.id.rvSpView)
        val sv = rootView.findViewById<SearchView>(R.id.svSpinner)
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapter
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        setSearchViewListener(adapter,list,sv)

        dialog.show()
    }

    private fun setSearchViewListener(adapter: RcViewDialogSpinnerAdapter, list: ArrayList<String>, sv: SearchView?) {
    sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            val tempList = UneverHelper.filterListData(list,p0)
            adapter.updateAdapter(tempList)
            return true
        }
    })
    }


}