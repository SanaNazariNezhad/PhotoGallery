package org.maktab.photogallery.adaptert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.maktab.photogallery.R;
import org.maktab.photogallery.databinding.ListItemPhotoGalleryBinding;
import org.maktab.photogallery.data.model.GalleryItem;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    private Context mContext;
    private List<GalleryItem> mItems;

    public List<GalleryItem> getItems() {
        return mItems;
    }

    public void setItems(List<GalleryItem> items) {
        mItems = items;
    }

    public PhotoAdapter(Context context, List<GalleryItem> items) {
        mContext = context;
        mItems = items;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemPhotoGalleryBinding binding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(mContext),
                        R.layout.list_item_photo_gallery,
                        parent,
                        false);
        return new PhotoHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.bindGalleryItem(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {

        private ListItemPhotoGalleryBinding mBinding;

        public PhotoHolder(ListItemPhotoGalleryBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
        }

        public void bindGalleryItem(GalleryItem item) {
            Glide.with(itemView)
                    .load(item.getUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_placeholder)
                    .into(mBinding.itemImageView);
        }
    }
}
