package com.example.androidpaging3.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.room.Room
import com.example.androidpaging3.R
import com.example.androidpaging3.data.RoomAdapter
import com.example.androidpaging3.databases.AppDatabase
import com.example.androidpaging3.databinding.ActivityRoomBinding
import com.example.androidpaging3.models.StoredObject
import com.example.androidpaging3.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "RoomActivity"

class RoomActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRoomBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private val roomAdapter = RoomAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_room)

        initViews()
    }

    private fun initViews() {
        prePopDB()
        mBinding.rvRoom.apply {
            setHasFixedSize(true)
            adapter = roomAdapter
        }

        observableData()
    }

    private fun prePopDB() {
        val dao = Room.databaseBuilder(this, AppDatabase::class.java, "myDB")
            .build()
            .storedObjectDao()
        lifecycleScope.launch(Dispatchers.IO) {
            for (i in 0..100) {
                val result = dao.insert(StoredObject(_id = 0, name = "name $i"))
                Log.e(TAG, "prePopDB: $result")
            }
        }
    }

    private fun observableData() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.itemsRoom.collectLatest {
                roomAdapter.submitData(it)
            }
        }
        roomAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
                showProgressBar(true)
            } else {
                showProgressBar(false)

                // getting the error
                val errorState = when {
                    loadState.prepend is LoadState.Error -> {
                        loadState.prepend as LoadState.Error
                    }
                    loadState.append is LoadState.Error -> {
                        loadState.append as LoadState.Error
                    }
                    loadState.refresh is LoadState.Error -> {
                        loadState.refresh as LoadState.Error
                    }
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this, it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showProgressBar(display: Boolean) {
        if (display)
            mBinding.progressBar.visibility = View.VISIBLE
        else
            mBinding.progressBar.visibility = View.GONE
    }
}