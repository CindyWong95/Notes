package com.example.cindywong.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.tom_roush.pdfbox.cos.COSDocument;
import com.tom_roush.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etNotes, etFile;
    Button btnSaveText, btnSavePDF, btnText, btnPDF;
    ListView listView;
    String[] SavedFiles; //Declaration to get a list of files
    Intent intent; // Declaration to go to next activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNotes=(EditText)findViewById(R.id.etNotes);
        etFile=(EditText)findViewById(R.id.etFile);
        btnSaveText=(Button)findViewById(R.id.btnSaveText);
        btnSavePDF=(Button)findViewById(R.id.btnSavePDF);
        btnText=(Button)findViewById(R.id.btnText);
        btnPDF=(Button)findViewById(R.id.btnPDF);
        listView=(ListView)findViewById(R.id.listViewFile);

        //Define to the next activity
        intent = new Intent(this, SavedNote.class);

        btnSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName=etFile.getText().toString();
                String notes=etNotes.getText().toString();
                try {
                    //Save in text file format
                    File root = new File(Environment.getExternalStorageDirectory()+"/Notes/");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File file = new File(root, fileName + ".txt");
                    FileWriter writer = new FileWriter(file);
                    writer.append(notes);
                    writer.flush();
                    writer.close();

                    //Show message when file save successful
                    Toast.makeText(getApplicationContext(), fileName+" saved as text in "+root.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    etNotes.setText(" ");
                    etFile.setText(" ");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSavePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName=etFile.getText().toString();
                String notes=etNotes.getText().toString();
                try {
                    //Save in PDF file format
                    File root = new File(Environment.getExternalStorageDirectory()+"/Notes/");
                    if (!root.exists()) {
                        root.mkdirs();
                    }
                    File file = new File(root, fileName + ".pdf");
                    OutputStream output = new FileOutputStream(file);
                    Document document = new Document();
                    PdfWriter.getInstance(document, output);
                    document.open();
                    Paragraph paragraph = new Paragraph(notes);
                    document.add(paragraph);
                    document.close();

                    //Show message when file save successful
                    Toast.makeText(getApplicationContext(), fileName +" Saved as pdf.", Toast.LENGTH_LONG).show();
                    etNotes.setText(" ");
                    etFile.setText(" ");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get file from directory
                File yourDir = new File(Environment.getExternalStorageDirectory() + "/Notes/");
                SavedFiles = yourDir.list();
                List files=new ArrayList<>();
                for(String i:SavedFiles){
                    if(i.endsWith(".txt")){
                        files.add(i);
                    }
                }
                //Call function to display all saved text file
                displayFileList(files);
            }
        });

        btnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get file from directory
                File yourDir = new File(Environment.getExternalStorageDirectory() + "/Notes/");
                SavedFiles = yourDir.list();
                List files=new ArrayList<>();
                for(String i:SavedFiles){
                    if(i.endsWith(".pdf")){
                        files.add(i);
                    }
                }
                //Call function to display all saved PDF file
                displayFileList(files);
            }
        });
    }

    public void displayFileList(List files){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, files);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String fileChosen;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get selected file name
                fileChosen = (String) listView.getItemAtPosition(position);

                try {
                    //Get file from directory
                    File file = new File(Environment.getExternalStorageDirectory() + "/Notes/",fileChosen);
                    String note=null;
                    if (fileChosen.endsWith(".txt")){
                        //Get text file data
                        StringBuilder txtData = new StringBuilder();
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            txtData.append(line+"\n");
                        }
                        note=txtData.toString();
                    } else if (fileChosen.endsWith(".pdf")){
                        //Get pdf file data
                        COSDocument cosDoc;
                        PDFParser parser = new PDFParser(new FileInputStream(file));
                        parser.parse();
                        cosDoc = parser.getDocument();
                        PDFTextStripper pdfStripper = new PDFTextStripper();
                        PDDocument pdDoc = new PDDocument(cosDoc);
                        pdfStripper.setStartPage(1);
                        pdfStripper.setEndPage(2);
                        String pdfData = pdfStripper.getText(pdDoc);

                        note=pdfData.toString();
                    }
                    //Pass data to new activity for display
                    intent.putExtra("fileChosen", fileChosen);
                    intent.putExtra("note", note);
                    startActivity(intent);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
