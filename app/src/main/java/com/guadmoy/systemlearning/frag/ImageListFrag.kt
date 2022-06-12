package com.guadmoy.systemlearning.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.guadmoy.systemlearning.R
import com.guadmoy.systemlearning.act.EditKyrsAct
import com.guadmoy.systemlearning.act.EditModuleActivity
import com.guadmoy.systemlearning.act.MyProfile
import com.guadmoy.systemlearning.databinding.ListImageFragBinding
import com.guadmoy.systemlearning.dialoghelper.ProgressDialog
import com.guadmoy.systemlearning.utils.AdapterCallback
import com.guadmoy.systemlearning.utils.ImageManager
import com.guadmoy.systemlearning.utils.ImagePicker
import com.guadmoy.systemlearning.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFrag(val fragmentCloseInterface: EditKyrsAct?) : Fragment(),
    AdapterCallback {
    lateinit var rootElement: ListImageFragBinding
    val adapter = SelectImageRvAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addItem: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootElement = ListImageFragBinding.inflate(inflater)
        return rootElement.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()

        touchHelper.attachToRecyclerView(rootElement.rcViewSelectImage)
        rootElement.rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
        rootElement.rcViewSelectImage.adapter = adapter
    }


    override fun onItemDelete() {
        addItem?.isVisible = true
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCloseInterface?.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    fun resizeSelectImage(newList: ArrayList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialogProgress = ProgressDialog.createProgressDialog(activity)
            val bitList = ImageManager.imageResize(newList, activity)
            dialogProgress.dismiss()
            adapter.updateAdapter(bitList, needClear)
            if (adapter.mainArray.size > 2) addItem?.isVisible = false
        }
    }

    private fun setUpToolbar() {
        rootElement.tbListImageFrag.inflateMenu(R.menu.menu_choose_image)
        val deleteItemAll = rootElement.tbListImageFrag.menu.findItem(R.id.id_delete_image_all)
        addItem = rootElement.tbListImageFrag.menu.findItem(R.id.id_add_image)
        if (adapter.mainArray.size > 2) addItem?.isVisible = false

        rootElement.tbListImageFrag.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteItemAll.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true)
            addItem?.isVisible = true
            true
        }

        addItem?.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.addImage(activity as EditKyrsAct, imageCount)
            true
        }
    }

    fun updateAdapter(newList: ArrayList<Uri>, activity: Activity) {
        resizeSelectImage(newList, false, activity)
    }

    fun setSingleImage(uri: Uri, pos: Int) {

        job = CoroutineScope(Dispatchers.Main).launch {
            val dialogProgress = ProgressDialog.createProgressDialog(activity as Activity)
            val bitList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            adapter.mainArray[pos] = bitList[0]
            dialogProgress.dismiss()
            adapter.notifyItemChanged(pos)
        }

    }


}