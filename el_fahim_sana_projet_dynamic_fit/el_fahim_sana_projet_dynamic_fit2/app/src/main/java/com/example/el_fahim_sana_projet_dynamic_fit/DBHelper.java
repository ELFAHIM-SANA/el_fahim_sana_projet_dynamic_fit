package com.example.el_fahim_sana_projet_dynamic_fit;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME="dynamic_fit.db";
    public DBHelper(@Nullable Context context) {
        super(context,DBNAME , null, 2);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT ,username TEXT NOT NULL UNIQUE,password TEXT ,nom TEXT  , genre TEXT ,telephone TEXT,image BLOB )");
        db.execSQL("CREATE TABLE activities(id INTEGER PRIMARY KEY AUTOINCREMENT, activity  TEXT NOT NULL,dateDebut DATETIME DEFAULT (datetime('now','localtime')),dateFin TEXT, id_user INTEGER NOT NULL)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS activities");
        onCreate(db);
    }


    public Boolean insertData(String username, String password, byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username",username);
        values.put("password",password);
        values.put("image",image);
        long result = db.insert("users",null,values);
        if(result == -1) return false;
        else return true;
    }

    public Boolean insertDataActivity(String activity, int id_user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("activity",activity);
        contentValues.put("id_user",id_user);
        //    contentValues.put("dateFin","");
        long result = db.insert("activities",null,contentValues);
        if(result == -1) return false;
        else return true;
    }
    public Boolean updateDataActivity(int id,String dateFin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("dateFin",dateFin);
        long result = db.update("activities",values,"id ="+id,null);
        if(result == -1) return false;
        else return true;
    }

    public int getIdDerAct(int id_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id from activities where id_user="+id_user, null);
        if (cursor.moveToLast()) {
            return cursor.getInt(0);
        } else {
            return 0;
        }
    }

    public String getDateFin(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> date_fin = new ArrayList<>();
        Cursor cursor = db.rawQuery("select dateFin from activities where id=" + id, null);
        if (cursor.moveToLast()) {
            date_fin.add(cursor.getString(0));
            if ((date_fin.size()>0)){
                return date_fin.get(date_fin.size() - 1);
            }else{
                return null;
            }
        } else {
            return null;
        }
    }


    public String getLastActivity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select activity from activities where id="+id, null);
        if (cursor.moveToLast()) {
            return cursor.getString(0);
        } else {
            return null;
        }
    }

    public Boolean updateData(int id, String password, String nom, String genre, String telephone, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("password", password);
            values.put("nom", nom);
            values.put("genre", genre);
            values.put("telephone", telephone);
            values.put("image", image);

            // Mettre à jour les données dans la table 'users' pour l'utilisateur avec l'ID spécifié
            long result = db.update("users", values, "id =" + id, null);

            // Vérifier si la mise à jour a été effectuée avec succès
            if (result != -1) {
                db.setTransactionSuccessful();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
        }
    }


    public Boolean updateDataImage(String username,byte[] image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("image",image);

        Cursor cursor = db.rawQuery("select id from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            int id = cursor.getInt(0);
            long result = db.update("users",values,"id ="+id,null);
            if(result == -1) return false;
            else return true;
        }else {
            return false;
        }
    }




    public Boolean checkusername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=?",new String[] {username});
        if(cursor.getCount()==0) return true;
        else  return  false;
    }

    public Boolean checkusernamepassword(String username,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        if(!checkusername(username)){
            Cursor cursor = db.rawQuery("select password from users where username=? ",new String[] {username});
            if (cursor.moveToLast()) {
                return  password.equals(cursor.getString(0));
            }else
                return false;
        }else
            return false;
    }

    public String getPassword(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select password from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            return cursor.getString(0);

        }else {
            return "error not found";
        }
    }


    public String getNom(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select nom from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            return cursor.getString(0);
        }else {
            return "error not found";
        }
    }

    public String getGenre(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select genre from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            return cursor.getString(0);

        }else {
            return "error not found";
        }
    }
    public String getTelephone(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select telephone from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            return cursor.getString(0);
        }else {
            return "error not found";

        }
    }

    public int getId(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id from users where username=?",new String[] {username});
        if (cursor.moveToLast()) {
            return cursor.getInt(0);
        }else {
            return 0;
        }
    }
    public byte[] getImage(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select image from users where id="+id,null);
        if (cursor.moveToLast()) {
            return cursor.getBlob(0);
        }else {
            return "No image".getBytes();

        }
    }
    public String getMajour(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select majour from users where username=?", new String[]{username});
        if (cursor.moveToLast()) {
            return cursor.getString(0);
        } else {
            return "error not found";
        }
    }


    Cursor displayAllActivity(int id_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery("select activity ,dateDebut ,dateFin from activities where id_user="+id_user , null);
        }
        return cursor;
    }

    public Boolean insertActivity(String activity, String dateDebut, String dateFin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("activity", activity);
        contentValues.put("dateDebut", dateDebut);
        contentValues.put("dateFin", dateFin);
        contentValues.put("id_user", Variables.id);

        long result = db.insert("activities", null, contentValues);
        return result != -1;
    }

    // Méthode pour obtenir l'activité avec la plus grande confiance pour un utilisateur donné
    public Cursor getMostConfidentActivity(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT activity, dateDebut, dateFin FROM activities WHERE id_user = ? ORDER BY confiance DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        return cursor;
    }


    public void deleteAllActivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("activities", null, null);
        db.close();
    }

}

