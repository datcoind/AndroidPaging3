package com.example.androidpaging3.data

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.androidpaging3.R
import com.example.androidpaging3.databinding.ItemRoomBinding
import com.example.androidpaging3.models.StoredObject

private const val TAG = "PhotoAdapter"

class RoomAdapter :
    PagingDataAdapter<StoredObject, RoomAdapter.ItemViewHolder>(StoredObjectComparator) {
    private var mContext: Context? = null

    object StoredObjectComparator : DiffUtil.ItemCallback<StoredObject>() {
        override fun areItemsTheSame(oldItem: StoredObject, newItem: StoredObject): Boolean {
            // Id is unique.
            return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: StoredObject, newItem: StoredObject): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        mContext = parent.context
        val view = DataBindingUtil.inflate<ItemRoomBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_room, parent, false
        )
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val mBinding: ItemRoomBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bindData(item: StoredObject) {
            mContext?.let {
                mBinding.tvName.text = item.name
            }
        }
    }
}