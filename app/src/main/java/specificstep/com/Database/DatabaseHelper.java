package specificstep.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import specificstep.com.Models.Color;
import specificstep.com.Models.Company;
import specificstep.com.Models.Default;
import specificstep.com.Models.Product;
import specificstep.com.Models.State;
import specificstep.com.Models.User;
import specificstep.com.Models.UserList;

/**
 * Created by ubuntu on 13/1/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;
     private static final int OLD_DATABASE_VERSION = 9;

    private static final String DATABASE_NAME = "RechargeEngine";

    private static final String TABLE_DEFAULT_SETTINGS = "default_settings";

    private static final String KEY_ID = "id";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_STATE_ID = "state_id";
    private static final String KEY_STATE_NAME = "state_name";

    private static final String TABLE_COMPANY = "company";

    private static final String KEY_COMPANY_ITEM_ID = "item_id";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_LOGO = "logo";
    private static final String KEY_SERVICE_TYPE = "service_type";

    private static final String TABLE_PRODUCT = "product";

    private static final String KEY_PRODUCT_ITEM_ID = "item_id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_PRODUCT_LOGO = "product_logo";

    private static final String TABLE_STATE = "state";

    private static final String KEY_CIRCLE_ID = "circle_id";
    private static final String KEY_CIRCLE_NAME = "circle_name";

    private static final String TABLE_USER = "user";

    private static final String KEY_OTP_CODE = "otp_code";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PWD = "password";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_REG_DATE = "reg_date";

    private static final String TABLE_STATUS_COLOR = "status_color";

    private static final String TABLE_USER_LIST = "user_list";

    private static final String KEY_COLOR_NAME = "name";
    private static final String KEY_COLOR_VALUE = "value";

    @Inject
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DEFAULT_SETTINGS = "CREATE TABLE " + TABLE_DEFAULT_SETTINGS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT," + KEY_STATE_ID + " TEXT," +
                KEY_STATE_NAME + " TEXT" + ")";

        String CREATE_TABLE_COMPANY = "CREATE TABLE " + TABLE_COMPANY + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_COMPANY_ITEM_ID + " TEXT," + KEY_COMPANY_NAME + " TEXT," +
                KEY_LOGO + " TEXT, " + KEY_SERVICE_TYPE + " TEXT" + ")";

        String CREATE_TABLE_PRODUCT = "CREATE TABLE " + TABLE_PRODUCT + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_PRODUCT_ITEM_ID + " TEXT," + KEY_PRODUCT_NAME + " TEXT," +
                KEY_COMPANY_ID + " TEXT," + KEY_PRODUCT_LOGO + " TEXT," + KEY_SERVICE_TYPE + " TEXT" + ")";

        String CREATE_TABLE_STATE = "CREATE TABLE " + TABLE_STATE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_CIRCLE_ID + " TEXT," + KEY_CIRCLE_NAME + " TEXT" + ")";

        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_ID + " TEXT," + KEY_OTP_CODE + " TEXT," +
                KEY_USER_NAME + " TEXT," + KEY_DEVICE_ID + " TEXT," + KEY_NAME + " TEXT," + KEY_PWD + " TEXT," + KEY_REMEMBER_ME + " TEXT," +
                KEY_REG_DATE + " TEXT" + ")";

        String CREATE_TABLE_STATUS_COLOR = "CREATE TABLE " + TABLE_STATUS_COLOR + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," + KEY_COLOR_NAME + " TEXT," + KEY_COLOR_VALUE + " TEXT" + ")";

        /* [START] - 2017_04_20 - Add new field in user list table */
        // Old table
        String CREATE_TABLE_USER_LIST = "CREATE TABLE " + TABLE_USER_LIST + "(" +
                KEY_ID + " INTEGER PRIMARY KEY, username TEXT, phone_no TEXT, email TEXT, usertype TEXT, ParentUserId TEXT)";
        // new table
//        String CREATE_TABLE_USER_LIST = "CREATE TABLE " + TABLE_USER_LIST + "(" +
//                KEY_ID + " INTEGER PRIMARY KEY, username TEXT, phone_no TEXT, email TEXT, usertype TEXT, firmName TEXT)";
        // [END]
        db.execSQL(CREATE_TABLE_DEFAULT_SETTINGS);
        db.execSQL(CREATE_TABLE_COMPANY);
        db.execSQL(CREATE_TABLE_PRODUCT);
        db.execSQL(CREATE_TABLE_STATE);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_STATUS_COLOR);
        db.execSQL(CREATE_TABLE_USER_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* [START] - 2017_04_20 - Add new field in user list table */
        // OLD logic
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPANY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS_COLOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_LIST);
        onCreate(db);
        // New logic - Add new column without remove all data
        Cursor mCursor = null;
        try {
            mCursor = db.rawQuery("SELECT * FROM " + TABLE_USER_LIST + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex("ParentUserId") != -1) {

            } else {
                String upgradeQuery = "ALTER TABLE " + TABLE_USER_LIST + " ADD COLUMN ParentUserId TEXT";
                if (oldVersion == OLD_DATABASE_VERSION && newVersion == DATABASE_VERSION) {
                    db.execSQL(upgradeQuery);
                    Log.d("DB", "SQLiteOpenHelper column added successfully.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        // [END]
    }

    public void addColors(List<Color> colorArrayList) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (int i = 0; i < colorArrayList.size(); i++) {
                values.put(KEY_COLOR_NAME, colorArrayList.get(i).getColor_name());
                values.put(KEY_COLOR_VALUE, colorArrayList.get(i).getColo_value());
                db.insert(TABLE_STATUS_COLOR, null, values);
            }
            db.close(); // Closing database connection
    }

    public void addDefaultSettings(String user_id, String state_id, String state_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_STATE_ID, state_id);
        values.put(KEY_STATE_NAME, state_name);
        db.insert(TABLE_DEFAULT_SETTINGS, null, values);
        db.close(); // Closing database connection
    }

    public void addUserDetails(String user_id, String otp_code, String user_name, String device_id, String name, String remember_me) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user_id);
        values.put(KEY_OTP_CODE, otp_code);
        values.put(KEY_USER_NAME, user_name);
        values.put(KEY_DEVICE_ID, device_id);
        values.put(KEY_NAME, name);
        values.put(KEY_REMEMBER_ME, remember_me);
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public void addCompanysDetails(List<Company> companyArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < companyArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_COMPANY_ITEM_ID, companyArrayList.get(i).getId());
            values.put(KEY_COMPANY_NAME, companyArrayList.get(i).getCompany_name());
            values.put(KEY_LOGO, companyArrayList.get(i).getLogo());
            values.put(KEY_SERVICE_TYPE, companyArrayList.get(i).getService_type());
            db.insert(TABLE_COMPANY, null, values);
        }
        db.close(); // Closing database connection
    }

    public void addProductsDetails(List<Product> productArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < productArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_PRODUCT_ITEM_ID, productArrayList.get(i).getId());
            values.put(KEY_PRODUCT_NAME, productArrayList.get(i).getProduct_name());
            values.put(KEY_COMPANY_ID, productArrayList.get(i).getCompany_id());
            values.put(KEY_PRODUCT_LOGO, productArrayList.get(i).getProduct_logo());
            values.put(KEY_SERVICE_TYPE, productArrayList.get(i).getService_type());
            db.insert(TABLE_PRODUCT, null, values);
        }
        db.close(); // Closing database connection
    }

    public void addStatesDetails(List<State> stateArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < stateArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_CIRCLE_ID, stateArrayList.get(i).getCircle_id());
            values.put(KEY_CIRCLE_NAME, stateArrayList.get(i).getCircle_name());
            db.insert(TABLE_STATE, null, values);
        }
        db.close(); // Closing database connection
    }

    public void addUserListDetails(List<UserList> userListArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < userListArrayList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put("id", userListArrayList.get(i).getUser_id());
            values.put("username", userListArrayList.get(i).getUser_name());
            values.put("email", userListArrayList.get(i).getEmail());
            values.put("phone_no", userListArrayList.get(i).getPhone_no());
            values.put("usertype", userListArrayList.get(i).getUsertype());
            values.put("ParentUserId", userListArrayList.get(i).getParentUserId());
            db.insert(TABLE_USER_LIST, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteDefaultSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_DEFAULT_SETTINGS);
        db.close();
    }

    public void deleteCompanyDetail(String service_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COMPANY, KEY_SERVICE_TYPE + "=?", new String[]{service_type});
        db.close();
    }

    public void deleteProductDetail(String service_type) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, KEY_SERVICE_TYPE + "=?", new String[]{service_type});
        db.close();
    }

    public void deleteStateDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STATE);
        db.close();
    }

    public void deleteStatusColor() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_STATUS_COLOR);
        db.close();
    }

    public void deleteUserListDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER_LIST);
        db.close();
    }

    public void deleteUsersDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }

//    public ArrayList<Company> getCompanyDetails(String Service_type) {
//        ArrayList<Company> companyArrayList = new ArrayList<Company>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(TABLE_COMPANY, new String[]{KEY_SERVICE_TYPE, KEY_COMPANY_NAME, KEY_LOGO, KEY_COMPANY_ITEM_ID}, KEY_SERVICE_TYPE + "=?",
//                new String[]{Service_type}, null, null, null, null);
//        // looping through all rows and adding to list
//        if (cursor != null) {
//            cursor.moveToFirst();
//            do {
//                Company company = new Company();
//                company.setId(cursor.getString(3));
//                company.setCompany_name(cursor.getString(1));
//                company.setLogo(cursor.getString(2));
//                company.setService_type(cursor.getString(0));
//                companyArrayList.add(company);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return companyArrayList;
//    }

//    public ArrayList<Product> getProductDetails(String company_id) {
//        ArrayList<Product> productArrayList = new ArrayList<Product>();
//        // Select All Query
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{KEY_PRODUCT_ITEM_ID, KEY_PRODUCT_NAME, KEY_COMPANY_ID, KEY_PRODUCT_LOGO}, KEY_COMPANY_ID + "=?",
//                new String[]{company_id}, null, null, null, null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                Product product = new Product();
//                product.setId(cursor.getString(0));
//                product.setProduct_name(cursor.getString(1));
//                product.setCompany_id(cursor.getString(2));
//                product.setProduct_logo(cursor.getString(3));
//                productArrayList.add(product);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return productArrayList;
//    }

//    public ArrayList<State> getStateDetails() {
//        ArrayList<State> stateArrayList = new ArrayList<State>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                State state = new State();
//                state.setCircle_id(cursor.getString(1));
//                state.setCircle_name(cursor.getString(2));
//                stateArrayList.add(state);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return stateArrayList;
//    }

    public ArrayList<UserList> getUserListDetails() {
        ArrayList<UserList> userListArrayList = new ArrayList<UserList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserList userList = new UserList();
                userList.setUser_id(cursor.getString(0));
                userList.setUser_name(cursor.getString(1));
                userList.setPhone_no(cursor.getString(2));
                userList.setEmail(cursor.getString(3));
                userList.setUsertype(cursor.getString(4));
                userList.setParentUserId(cursor.getString(5));
                userListArrayList.add(userList);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return userListArrayList;
    }

    public ArrayList<UserList> getUserListDetailsByType(String usertype) {
        ArrayList<UserList> userListArrayList = new ArrayList<UserList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST + " WHERE usertype='" + usertype + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserList userList = new UserList();
                userList.setUser_id(cursor.getString(0));
                userList.setUser_name(cursor.getString(1));
                userList.setPhone_no(cursor.getString(2));
                userList.setEmail(cursor.getString(3));
                userList.setUsertype(cursor.getString(4));
                userList.setParentUserId(cursor.getString(5));
                userListArrayList.add(userList);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return userListArrayList;
    }

//    public ArrayList<UserList> getAllChildUser() {
//        ArrayList<UserList> userListArrayList = new ArrayList<UserList>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                UserList userList = new UserList();
//                userList.setUser_id(cursor.getString(0));
//                userList.setUser_name(cursor.getString(1));
//                userList.setPhone_no(cursor.getString(2));
//                userList.setEmail(cursor.getString(3));
//                userList.setUsertype(cursor.getString(4));
//                userListArrayList.add(userList);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return userListArrayList;
//    }

//    public ArrayList<UserList> getUserListDetailsById(String id) {
//        ArrayList<UserList> userListArrayList = new ArrayList<UserList>();
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST + " WHERE id='" + id + "'";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            do {
//                UserList userList = new UserList();
//                userList.setUser_id(cursor.getString(0));
//                userList.setUser_name(cursor.getString(1));
//                userList.setPhone_no(cursor.getString(2));
//                userList.setEmail(cursor.getString(3));
//                userList.setUsertype(cursor.getString(4));
//                userListArrayList.add(userList);
//            }
//            while (cursor.moveToNext());
//        }
//        db.close();
//        return userListArrayList;
//    }

    public ArrayList<UserList> getUserListDetailsById() {
        ArrayList<UserList> userListArrayList = new ArrayList<UserList>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UserList userList = new UserList();
                userList.setUser_id(cursor.getString(0));
                userList.setUser_name(cursor.getString(1));
                userList.setPhone_no(cursor.getString(2));
                userList.setEmail(cursor.getString(3));
                userList.setUsertype(cursor.getString(4));
                userList.setParentUserId(cursor.getString(5));
                userListArrayList.add(userList);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return userListArrayList;
    }

//    public UserList getUserListDetails(String username) {
//        UserList userList = new UserList();
//        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST + " WHERE username='" + username + "'";
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst()) {
//            userList.setUser_id(cursor.getString(0));
//            userList.setUser_name(cursor.getString(1));
//            userList.setPhone_no(cursor.getString(2));
//            userList.setEmail(cursor.getString(3));
//            userList.setUsertype(cursor.getString(4));
//        } else {
//            userList = null;
//        }
//        db.close();
//        return userList;
//    }

    public UserList getUserListDetailsByPhoneNumber(String phoneNumber) {
        UserList userList = new UserList();
        String selectQuery = "SELECT  * FROM " + TABLE_USER_LIST + " WHERE phone_no='" + phoneNumber + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            userList.setUser_id(cursor.getString(0));
            userList.setUser_name(cursor.getString(1));
            userList.setPhone_no(cursor.getString(2));
            userList.setEmail(cursor.getString(3));
            userList.setUsertype(cursor.getString(4));
            userList.setParentUserId(cursor.getString(5));
        } else {
            userList = null;
        }
        db.close();
        return userList;
    }

    public ArrayList<Default> getDefaultSettings() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ArrayList<Default> defaultArrayList = new ArrayList<Default>();
        Cursor cursor = sqLiteDatabase.query(TABLE_DEFAULT_SETTINGS, new String[]{KEY_ID,
                        KEY_USER_ID, KEY_STATE_ID, KEY_STATE_NAME}, null,
                null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Default aDefault = new Default();
            aDefault.setUser_id(cursor.getString(1));
            aDefault.setState_id(cursor.getString(2));
            aDefault.setState_name(cursor.getString(3));
            defaultArrayList.add(aDefault);
        }
        sqLiteDatabase.close();
        return defaultArrayList;
    }

//    public String getCircleID(String circle_name) {
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.query(TABLE_STATE, new String[]{KEY_CIRCLE_ID}, KEY_CIRCLE_NAME + "=?",
//                new String[]{circle_name}, null, null, null, null);
//        String circle_id = null;
//        if (cursor != null) {
//            cursor.moveToFirst();
//            circle_id = cursor.getString(0);
//        }
//        sqLiteDatabase.close();
//        return circle_id;
//    }

    public ArrayList<Color> getAllColors() {
        ArrayList<Color> textColorArrayList = new ArrayList<Color>();
        String selectQuery = "SELECT  * FROM " + TABLE_STATUS_COLOR;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Color textColor = new Color();
                textColor.setColor_name(cursor.getString(1));
                textColor.setColo_value(cursor.getString(2));
                textColorArrayList.add(textColor);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return textColorArrayList;
    }

//    public String getCompanyLogo(String company_name) {
//        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
//        Cursor cursor = sqLiteDatabase.query(TABLE_COMPANY, new String[]{KEY_LOGO}, KEY_COMPANY_NAME + "=?",
//                new String[]{company_name}, null, null, null, null);
//        String company_logo = null;
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            company_logo = cursor.getString(0);
//        }
//        sqLiteDatabase.close();
//        return company_logo;
//    }

    public ArrayList<User> getUserDetail() {
        ArrayList<User> userArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String select_query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = sqLiteDatabase.rawQuery(select_query, null);
        if (cursor != null) {
            cursor.moveToFirst();
            User user = new User();
            try {
                user.setUser_id(cursor.getString(1));
                user.setOtp_code(cursor.getString(2));
                user.setUser_name(cursor.getString(3));
                user.setDevice_id(cursor.getString(4));
                user.setName(cursor.getString(5));
                user.setPassword(cursor.getString(6));
                user.setRemember_me(cursor.getString(7));
                user.setReg_date(cursor.getString(8));
                userArrayList.add(user);
            } catch (Exception e) {
                System.out.println("Database: " + e.toString());
            }
        }
        sqLiteDatabase.close();
        return userArrayList;
    }

    public User getUserDetails() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String select_query = "SELECT * FROM " + TABLE_USER;
        Cursor cursor = sqLiteDatabase.rawQuery(select_query, null);
        User user = new User();
        if (cursor != null) {
            cursor.moveToFirst();
            user.setOtp_code(cursor.getString(2));
            user.setUser_name(cursor.getString(3));
            user.setDevice_id(cursor.getString(4));
            user.setName(cursor.getString(5));
            user.setPassword(cursor.getString(6));
            user.setRemember_me(cursor.getString(7));
            user.setReg_date(cursor.getString(8));
            cursor.close();
        }
        sqLiteDatabase.close();
        return user;
    }

    public int updateUserDetails(String uname, String pwd, String remember_me_status, String reg_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PWD, pwd);
        values.put(KEY_REMEMBER_ME, remember_me_status);
        values.put(KEY_REG_DATE, reg_date);
        // updating row
        return db.update(TABLE_USER, values, KEY_USER_NAME + " = ?",
                new String[]{String.valueOf(uname)});
    }
}
