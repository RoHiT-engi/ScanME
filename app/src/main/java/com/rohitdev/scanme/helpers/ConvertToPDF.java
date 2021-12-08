package com.rohitdev.scanme.helpers;


import android.content.Context;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ConvertToPDF {
    private Context AppContext;
    private File[] imagesFiles;
    private String PdfName;

    public ConvertToPDF(Context context, File[] imagesFiles,String PdfName){
        AppContext= context;
        this.imagesFiles=imagesFiles;
        this.PdfName = PdfName;
    }

    public void makepdf() throws IOException, DocumentException {
        Document document = new Document();

        String app_folder_path = String.valueOf(AppContext.getApplicationContext().getExternalMediaDirs()[0]+"/pdf");
        File folder=new File(app_folder_path);
        if(!folder.exists()) {
            boolean bool = folder.mkdir();
        }

        try {
            PdfWriter.getInstance(document, new FileOutputStream(app_folder_path+"/" + PdfName +".pdf")); //  Change pdf's name.
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        document.open();

        for(int i=0;i<imagesFiles.length;i++){
            Image image = Image.getInstance(imagesFiles[i].getAbsolutePath());  // Change image's name and extension.
            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            document.add(image);


        }
        Toast.makeText(AppContext,"PDF created Successfully",Toast.LENGTH_SHORT).show();
        document.close();
    }

}
