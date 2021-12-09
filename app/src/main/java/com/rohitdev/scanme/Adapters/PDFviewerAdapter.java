package com.rohitdev.scanme.Adapters;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.rohitdev.scanme.R;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
        Log.e("Check URi",images[position].getAbsolutePath());
        Uri content_uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", images[position]);
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(holder.intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, content_uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        holder.intent.setDataAndType(content_uri ,"application/pdf");
        holder.shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(images[position].getAbsolutePath()));

    }

    @Override
    public int getItemCount() {
        return null!=images?images.length:0;
    }

    public static class PDFviewerViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTextView;
        private Intent intent,shareIntent,share;
        private ImageView imageView;
        public PDFviewerViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.PDF_name);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(intent);
                }
            });
            shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            imageView = (ImageView) itemView.findViewById(R.id.share_button);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share = Intent.createChooser(shareIntent,null);
                    context.startActivity(share);
                }
            });
        }
    }
    public void setImages(File[] set_images){
        images = set_images;
        notifyDataSetChanged();
    }
}
