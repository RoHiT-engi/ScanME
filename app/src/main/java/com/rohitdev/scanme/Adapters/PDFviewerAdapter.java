package com.rohitdev.scanme.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitdev.scanme.R;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PDFviewerAdapter extends RecyclerView.Adapter<PDFviewerAdapter.PDFviewerViewHolder> {

    File[] images =null;
    @SuppressLint("StaticFieldLeak")
    static Context context = null;

    public PDFviewerAdapter(Context context){
        PDFviewerAdapter.context = context;
    }

    @NonNull
    @Override
    public PDFviewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =parent.getContext();
        int layoutResID = R.layout.list_item_pdfviewer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParent = false;
        View view =inflater.inflate(layoutResID,parent,attachToParent);
        return new PDFviewerViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PDFviewerViewHolder holder, int position) {

        Path path = Paths.get(images[position].getAbsolutePath());
        Path fileName = path.getFileName();
        String DisplayText = String.valueOf(fileName.getFileName());
        holder.mTextView.setText(DisplayText);
        holder.intent.setDataAndType(FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",images[position]),"application/pdf");

    }

    @Override
    public int getItemCount() {
        return null!=images?images.length:0;
    }

    public static class PDFviewerViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTextView;
        private Intent intent;
        public PDFviewerViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.PDF_name);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(intent);
                }
            });
        }
    }
    public void setImages(File[] set_images){
        images = set_images;
        notifyDataSetChanged();
    }
}
