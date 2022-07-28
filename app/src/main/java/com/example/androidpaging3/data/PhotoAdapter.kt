package com.example.androidpaging3.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidpaging3.R
import com.example.androidpaging3.databinding.ItemPhotoBinding
import com.example.androidpaging3.models.PhotoModel

private const val TAG = "PhotoAdapter"

class PhotoAdapter : PagingDataAdapter<PhotoModel, PhotoAdapter.ItemViewHolder>(PhotoComparator) {
    private var mContext: Context? = null

    object PhotoComparator : DiffUtil.ItemCallback<PhotoModel>() {
        override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
            // Id is unique.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        val view = DataBindingUtil.inflate<ItemPhotoBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_photo, parent, false
        )
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val mBinding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(item: PhotoModel) {
            mContext?.let {
                mBinding.photoModel = item
                mBinding.tvPosition.text = "- Position: $layoutPosition"
                Glide.with(it).load(item.pathFile).into(mBinding.ivThumb)
            }
        }
    }
}