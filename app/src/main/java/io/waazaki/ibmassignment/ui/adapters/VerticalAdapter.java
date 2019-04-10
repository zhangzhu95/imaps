package io.waazaki.ibmassignment.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import io.waazaki.ibmassignment.R;
import io.waazaki.ibmassignment.objects.Business;
import io.waazaki.ibmassignment.utils.RoundedCornersTransformation;

public class VerticalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Business> businessList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public VerticalAdapter(List<Business> businessList) {
        this.businessList = businessList;
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView mImageViewPicture;
        TextView mTextViewTitle;
        AppCompatRatingBar mRatingBarRate;
        TextView mTextViewRating;
        TextView mTextViewAddress;

        private VerticalViewHolder(View v) {
            super(v);
            mImageViewPicture = v.findViewById(R.id.image_view_picture);
            mTextViewTitle = v.findViewById(R.id.text_view_title);
            mRatingBarRate = v.findViewById(R.id.rating_bar_rate);
            mTextViewRating = v.findViewById(R.id.text_view_rating);
            mTextViewAddress = v.findViewById(R.id.text_view_address);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_vertical, parent, false);
        vh = new VerticalViewHolder(v);
        return vh;
    }

    public void setData(List<Business> businessList){
        this.businessList = businessList;
        notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof VerticalViewHolder) {
            VerticalViewHolder view = (VerticalViewHolder) holder;
            final Business business = businessList.get(position);
            int size = 140;
            RequestCreator requestCreator;
            RoundedCornersTransformation transformation = new RoundedCornersTransformation(10 , 10);

            //Click on item
            view.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null)
                        onItemClickListener.onItemClick(business);
                }
            });

            //Picture
            if(business.getImage_url() != null && !business.getImage_url().isEmpty())
                requestCreator = Picasso.get().load(business.getImage_url());
            else
                requestCreator = Picasso.get().load(R.mipmap.placeholder);

            requestCreator.resize(size, size)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.mipmap.placeholder)
                    .transform(transformation)
                    .centerCrop()
                    .into(view.mImageViewPicture);

            //Title
            if(business.getName() != null){
                view.mTextViewTitle.setText(String.valueOf(position + 1 + " - " + business.getName()));
            }else{
                view.mTextViewTitle.setText(String.valueOf(position + 1 + " - Empty"));
            }

            //Rating
            view.mRatingBarRate.setRating(business.getRating());

            //Reviews
            view.mTextViewRating.setText(String.valueOf(business.getReview_count() + " reviews"));

            //Address
            if(business.getLocation() != null && business.getLocation().getAddress1() != null){
                view.mTextViewAddress.setText(business.getLocation().getAddress1());
            }else{
                view.mTextViewAddress.setText(R.string.no_address);
            }

        }
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Business obj);
    }
}
