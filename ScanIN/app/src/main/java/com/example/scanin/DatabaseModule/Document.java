package com.example.scanin.DatabaseModule;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "document")
public class Document {

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="document_id")
    private long documentId;
    @ColumnInfo(name="document_name")
    private String documentName;

    public Document(String documentName1){
        documentName = documentName1;
    }

    public Document(){

    }

    public Document(long id, String documentName1){
        documentId = id;
        documentName = documentName1;
    }

    public long getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
