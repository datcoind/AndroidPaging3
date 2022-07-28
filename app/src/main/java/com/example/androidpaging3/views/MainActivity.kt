package com.example.androidpaging3.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.androidpaging3.R
import com.example.androidpaging3.data.PhotoAdapter
import com.example.androidpaging3.databinding.ActivityMainBinding
import com.example.androidpaging3.viewmodels.MainViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var photoAdapter = PhotoAdapter()
    private var coroutineJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        observableData()
    }

    private fun initViews() {
        mBinding.rvPhoto.apply {
            setHasFixedSize(true)
            adapter = photoAdapter
        }

        mBinding.btnRoom.setOnClickListener {
            startActivity(Intent(this, RoomActivity::class.java))
        }
    }

    private fun observableData() {
        coroutineJob?.cancel()
        coroutineJob = lifecycleScope.launch {
            viewModel.photos.collectLatest {
                delay(2000)
                photoAdapter.submitData(it)
            }
        }
        photoAdapter.addLoadStateListener { loadState ->
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