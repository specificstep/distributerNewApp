package specificstep.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.Models.ChildUserModel;

/**
 * Created by ubuntu on 3/5/17.
 */

@Singleton
public class ChildUserTable {
    /* [START] - Child user table name and field */
    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "userName";
    public static final String KEY_PHONENO = "phoneNo";
    public static final String KEY_FIRMNAME = "firmName";
    public static final String KEY_USERTYPE = "userType";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PARENTUSERID = "ParentUserId";

    private static final String DATABASE_NAME = "childuser";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "tbl_childuser";
    private static final String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_USERNAME + " TEXT,"
            + KEY_PHONENO + " TEXT,"
            + KEY_FIRMNAME + " TEXT,"
            + KEY_USERTYPE + " TEXT,"
            + KEY_BALANCE + " INTEGER,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PARENTUSERID + " TEXT"
            + ")";
    private OpenHelper openHelper;
    /*+ KEY_BALANCE + " TEXT,"*/

    @Inject
    public ChildUserTable(Context context) {
        openHelper = new OpenHelper(context);
        createTable();
    }

    private void createTable() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            db.execSQL(createTable);
            openHelper.close();
            //db.close();
        } catch (Exception ignored) {
        }
    }

    public void insert(ChildUserModel model) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_ID, model.id);
        values.put(KEY_USERNAME, model.userName);
        values.put(KEY_PHONENO, model.phoneNo);
        values.put(KEY_FIRMNAME, model.firmName);
        values.put(KEY_USERTYPE, model.userType);
        values.put(KEY_BALANCE, model.balance);
        values.put(KEY_EMAIL, model.email);
        values.put(KEY_PARENTUSERID, model.ParentUserId);
        Log.d("DB", "Insert data : " + db.insert(TABLE_NAME, null, values));
        openHelper.close();
        //db.close();
    }

    public void update(ChildUserModel model) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_ID, model.id);
        values.put(KEY_USERNAME, model.userName);
        values.put(KEY_PHONENO, model.phoneNo);
        values.put(KEY_FIRMNAME, model.firmName);
        values.put(KEY_USERTYPE, model.userType);
        values.put(KEY_BALANCE, model.balance);
        values.put(KEY_EMAIL, model.email);
        values.put(KEY_PARENTUSERID, model.ParentUserId);
        Log.d("DB", "Update data : " + db.update(TABLE_NAME, values, null, null));
        openHelper.close();
        //db.close();
    }

    public void delete_All() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        openHelper.close();
        db.close();
    }

    //    select * from product order by item_id desc
//    select * from product WHERE item_id BETWEEN 1 AND 10
//    select * from product WHERE item_id BETWEEN 1 AND 10 order by item_id desc
    // select * from product WHERE item_id > 80
    // // select * from product WHERE KEY_BALANCE > 80 " order by " + KEY_FIRMNAME + " COLLATE NOCASE"
    // select * from product WHERE item_id BETWEEN 1 AND 80 order by id
    // HIGH - LOW select * from product WHERE item_id BETWEEN 1 AND 80 order by id DESC
    // LOW - HIGH = select * from product WHERE item_id BETWEEN 1 AND 80 order by id ASC
    public ArrayList<ChildUserModel> select_Data(String whereClause) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            Log.d("DB", "Select Data Where Clause : " + "select * from " + TABLE_NAME + " where " + whereClause);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where " + whereClause, null);
            return Load_DATA(cursor);
        } finally {
            db.close();
        }
    }

    public ArrayList<ChildUserModel> selectData_OrderByBalance(String orderBy) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            Log.d("DB", "select * from " + TABLE_NAME + " ORDER BY " + orderBy);
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " ORDER BY " + orderBy, null);
            return Load_DATA(cursor);
        } finally {
            db.close();
        }
    }

    public ArrayList<ChildUserModel> select_Data() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
            return Load_DATA(cursor);
        } finally {
            db.close();
        }
    }

    private ArrayList<ChildUserModel> Load_DATA(Cursor cursor) {
        ArrayList<ChildUserModel> models = new ArrayList<ChildUserModel>();
        if (cursor.moveToFirst()) {
            do {
                ChildUserModel model = new ChildUserModel();
                model.id = cursor.getString(cursor.getColumnIndex(KEY_ID));
                model.userName = cursor.getString(cursor.getColumnIndex(KEY_USERNAME));
                model.phoneNo = cursor.getString(cursor.getColumnIndex(KEY_PHONENO));
                model.firmName = cursor.getString(cursor.getColumnIndex(KEY_FIRMNAME));
                model.userType = cursor.getString(cursor.getColumnIndex(KEY_USERTYPE));
                // model.balance= cursor.getString(cursor.getColumnIndex(KEY_BALANCE));
                model.balance = cursor.getFloat(cursor.getColumnIndex(KEY_BALANCE));
                model.email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
                model.ParentUserId = cursor.getString(cursor.getColumnIndex(KEY_PARENTUSERID));
                models.add(model);
            }
            while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        openHelper.close();
        return models;
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                // Log.d("DB", "Create table : " + createTable);
                db.execSQL(createTable);
            } catch (Exception ignored) {
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d("DB", "Upgrading database, this will drop login tables and recreate.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
