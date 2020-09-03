package com.example.wsr_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;



    ImageAdapter(Context context, List<Upload> uploads){

        mContext = context;     //no this

        mUploads = uploads;     //no this
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        final String lat=uploadCurrent.getLat();
        final String lon=uploadCurrent.getLon();
        String s = "Location: ("+lat+","+lon+") \n"+uploadCurrent.gettime();
        holder.textViewName.setText(s);

        // holder.imageView.setImageURI(Uri.parse(uploadCurrent.getImageUrl()));
        Picasso.get().load(uploadCurrent.getImageUrl()).resize(1300,700).into(holder.imageView);
        // Picasso.with(this.mContext).load(uploadCurrent.getImageUrl()).into(holder.imageView);

        holder.btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://www.google.com/maps/search/?api=1&query="+lat+","+lon;
                Intent browserIntent=new Intent(Intent.ACTION_VIEW);
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                browserIntent.setData(Uri.parse(link));
                mContext.startActivity(browserIntent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName;
        ImageView imageView;
        Button btnmap;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.text_view_loc);
            imageView = (ImageView) itemView.findViewById(R.id.image_view_upload);
            btnmap = (Button) itemView.findViewById(R.id.btnmap);

        }
    }
}
