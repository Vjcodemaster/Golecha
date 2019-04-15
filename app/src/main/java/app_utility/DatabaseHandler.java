package app_utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static android.app.DownloadManager.COLUMN_ID;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "GOLECHA_DB";

    private static final String TABLE_PRODUCTS = "TABLE_PRODUCTS";

    private static final String TABLE_TEMP_PRODUCTS = "TABLE_TEMP_PRODUCTS";

    private static final String TABLE_PRODUCTS_DATA = "TABLE_PRODUCTS_DATA";

    private static final String KEY_ID = "_id";

    private static final String KEY_ODOO_ID = "KEY_ODOO_ID";

    private static final String KEY_SALES_ORDER_ID = "KEY_SALES_ORDER_ID";

    private static final String KEY_SALES_ORDER_LINE_ID = "KEY_SALES_ORDER_LINE_ID";

    private static final String KEY_PRODUCT_ID = "KEY_PRODUCT_ID";

    private static final String KEY_PRODUCT_NAME = "KEY_PRODUCT_NAME";

    private static final String KEY_QUANTITY = "KEY_QUANTITY";

    private static final String KEY_UNIT_PRICE = "KEY_UNIT_PRICE";

    private static final String KEY_SUB_TOTAL = "KEY_SUB_TOTAL";

    private static final String KEY_STATUS = "KEY_STATUS";

    private static final String KEY_DELIVERY_DATE = "KEY_DELIVERY_DATE";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    /*
    leaving gap between "CREATE TABLE" & TABLE_RECENT gives error watch out!
    Follow the below format
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_INDIVIDUAL_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_ODOO_ID + " INTEGER, "
                + KEY_SALES_ORDER_ID + " TEXT, "
                //+ KEY_SALES_ORDER_LINE_ID + " TEXT, "
                + KEY_PRODUCT_ID + " TEXT, "
                + KEY_PRODUCT_NAME + " TEXT, "
                + KEY_QUANTITY + " TEXT, "
                + KEY_UNIT_PRICE + " TEXT, "
                + KEY_SUB_TOTAL + " TEXT, "
                + KEY_STATUS + " TEXT)";

        String CREATE_TEMP_PRODUCTS = "CREATE TABLE " + TABLE_TEMP_PRODUCTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PRODUCT_ID + " TEXT, "
                + KEY_PRODUCT_NAME + " TEXT, "
                + KEY_QUANTITY + " TEXT, "
                + KEY_UNIT_PRICE + " TEXT, "
                + KEY_SUB_TOTAL + " TEXT, "
                + KEY_DELIVERY_DATE + " TEXT, "
                + KEY_STATUS + " TEXT)";

        String CREATE_PRODUCTS_DATA_TABLE = "CREATE TABLE " + TABLE_PRODUCTS_DATA + "("
                + KEY_ID + " INTEGER PRIMARY KEY, "
                + KEY_PRODUCT_ID + " INTEGER, "
                + KEY_PRODUCT_NAME + " TEXT, "
                + KEY_UNIT_PRICE + " TEXT)";
        //+ KEY_INDIVIDUAL_PRODUCT_VARIANT_NAMES + " TEXT, "
        //+ KEY_INDIVIDUAL_PRODUCT_VARIANT_IMAGES + " TEXT)";

        db.execSQL(CREATE_PRODUCTS_DATA_TABLE);
        db.execSQL(CREATE_TEMP_PRODUCTS);
        db.execSQL(CREATE_INDIVIDUAL_PRODUCTS);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        // Create tables again
        onCreate(db);
    }

    // Adding new data
    public void addDataToProductsTable(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, dataBaseHelper.get_id());
        values.put(KEY_ODOO_ID, dataBaseHelper.get_odoo_id());
        values.put(KEY_SALES_ORDER_ID, dataBaseHelper.get_sales_order_id());
        //values.put(KEY_SALES_ORDER_LINE_ID, dataBaseHelper.get_sales_order_line_id());
        values.put(KEY_PRODUCT_ID, dataBaseHelper.get_product_id_string());
        values.put(KEY_PRODUCT_NAME, dataBaseHelper.get_product_name());
        values.put(KEY_QUANTITY, dataBaseHelper.get_product_quantity_string());
        values.put(KEY_UNIT_PRICE, dataBaseHelper.get_unit_price_string());
        values.put(KEY_SUB_TOTAL, dataBaseHelper.get_sub_total_string());
        values.put(KEY_STATUS, dataBaseHelper.get_order_status());

        db.insert(TABLE_PRODUCTS, null, values);
        //db.insert(TABLE_PERMANENT, null, values);

        db.close(); // Closing database connection
    }

    public void addDataToTempTable(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, dataBaseHelper.get_id());
        values.put(KEY_PRODUCT_ID, dataBaseHelper.get_product_id_string());
        values.put(KEY_PRODUCT_NAME, dataBaseHelper.get_product_name());
        values.put(KEY_QUANTITY, dataBaseHelper.get_product_quantity_string());
        values.put(KEY_UNIT_PRICE, dataBaseHelper.get_unit_price_string());
        values.put(KEY_SUB_TOTAL, dataBaseHelper.get_sub_total_string());
        values.put(KEY_DELIVERY_DATE, dataBaseHelper.get_delivery_date());
        values.put(KEY_STATUS, dataBaseHelper.get_order_status());

        // Inserting Row
        //db.insert(TABLE_RECENT, null, values);
        db.insert(TABLE_TEMP_PRODUCTS, null, values);

        db.close(); // Closing database connection
    }

    public void addProductsData(DataBaseHelper dataBaseHelper) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put(KEY_ID, dataBaseHelper.get_id());
        values.put(KEY_PRODUCT_ID, dataBaseHelper.get_product_id());
        values.put(KEY_PRODUCT_NAME, dataBaseHelper.get_product_name());
        values.put(KEY_UNIT_PRICE, dataBaseHelper.get_unit_price_string());

        // Inserting Row
        //db.insert(TABLE_RECENT, null, values);
        db.insert(TABLE_PRODUCTS_DATA, null, values);

        db.close(); // Closing database connection
    }

    public int lastID() {
        int res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, new String[]{COLUMN_ID,
        }, null, null, null, null, null);
        cursor.moveToLast();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            res = -1;
        }
        cursor.close();
        return res;
    }

    public int lastIDOfMainProducts() {
        int res;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCTS, new String[]{COLUMN_ID,
        }, null, null, null, null, null);
        cursor.moveToLast();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            res = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        } else {
            res = -1;
        }
        cursor.close();
        return res;
    }

    public List<DataBaseHelper> getProductsData() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        //ArrayList<String> alTechSpecs = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_INDIVIDUAL_PRODUCTS;
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS_DATA;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_id(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.set_product_id_string(cursor.getString(1));
                dataBaseHelper.set_product_name(cursor.getString(2));
                dataBaseHelper.set_unit_price_string(cursor.getString(3));
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    public List<DataBaseHelper> getProductsByrStatusFilter(String sKey) {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        //ArrayList<String> alTechSpecs = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_INDIVIDUAL_PRODUCTS;
        String selectQuery = "SELECT  * FROM " + TABLE_TEMP_PRODUCTS + " WHERE "
                + KEY_STATUS + "=" + sKey;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_id(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.set_product_id_string(cursor.getString(1));
                dataBaseHelper.set_product_name(cursor.getString(2));
                dataBaseHelper.set_product_quantity_string(cursor.getString(3));
                dataBaseHelper.set_unit_price_string(cursor.getString(4));
                dataBaseHelper.set_sub_total_string(cursor.getString(5));
                /*dataBaseHelper.set_main_product_id(cursor.getInt(1));
                dataBaseHelper.set_main_product_names(cursor.getString(2));*/
                /*dataBaseHelper.set_individual_product_names(cursor.getString(4));
                dataBaseHelper.set_individual_product_description(cursor.getString(5));
                dataBaseHelper.set_individual_product_address(cursor.getString(6));
                dataBaseHelper.set_individual_product_images_path(cursor.getString(7));*/
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
                //String s = String.valueOf(dataBaseHelperList.get(cursor.getPosition()).get_individual_product_names());
                //alTechSpecs.add(s);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    public List<DataBaseHelper> getProductsFromTempProducts() {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        //ArrayList<String> alTechSpecs = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_INDIVIDUAL_PRODUCTS;
        String selectQuery = "SELECT  * FROM " + TABLE_TEMP_PRODUCTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_id(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.set_product_id_string(cursor.getString(1));
                dataBaseHelper.set_product_name(cursor.getString(2));
                dataBaseHelper.set_product_quantity_string(cursor.getString(3));
                dataBaseHelper.set_unit_price_string(cursor.getString(4));
                dataBaseHelper.set_sub_total_string(cursor.getString(5));
                dataBaseHelper.set_delivery_date(cursor.getString(6));
                dataBaseHelper.set_order_status(cursor.getString(7));
                /*dataBaseHelper.set_main_product_id(cursor.getInt(1));
                dataBaseHelper.set_main_product_names(cursor.getString(2));*/
                /*dataBaseHelper.set_individual_product_names(cursor.getString(4));
                dataBaseHelper.set_individual_product_description(cursor.getString(5));
                dataBaseHelper.set_individual_product_address(cursor.getString(6));
                dataBaseHelper.set_individual_product_images_path(cursor.getString(7));*/
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
                //String s = String.valueOf(dataBaseHelperList.get(cursor.getPosition()).get_individual_product_names());
                //alTechSpecs.add(s);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    public List<DataBaseHelper> getSingleProductByID(int ID) {
        List<DataBaseHelper> dataBaseHelperList = new ArrayList<>();
        //ArrayList<String> alTechSpecs = new ArrayList<>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + TABLE_INDIVIDUAL_PRODUCTS;
        String selectQuery = "SELECT  * FROM " + TABLE_TEMP_PRODUCTS + " WHERE " + KEY_ID + "=" + ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataBaseHelper dataBaseHelper = new DataBaseHelper();
                dataBaseHelper.set_id(Integer.parseInt(cursor.getString(0)));
                dataBaseHelper.set_product_id_string(cursor.getString(1));
                dataBaseHelper.set_product_name(cursor.getString(2));
                dataBaseHelper.set_product_quantity_string(cursor.getString(3));
                dataBaseHelper.set_unit_price_string(cursor.getString(4));
                dataBaseHelper.set_sub_total_string(cursor.getString(5));
                dataBaseHelper.set_delivery_date(cursor.getString(6));
                dataBaseHelper.set_order_status(cursor.getString(7));
                /*dataBaseHelper.set_main_product_id(cursor.getInt(1));
                dataBaseHelper.set_main_product_names(cursor.getString(2));*/
                /*dataBaseHelper.set_individual_product_names(cursor.getString(4));
                dataBaseHelper.set_individual_product_description(cursor.getString(5));
                dataBaseHelper.set_individual_product_address(cursor.getString(6));
                dataBaseHelper.set_individual_product_images_path(cursor.getString(7));*/
                // Adding data to list
                dataBaseHelperList.add(dataBaseHelper);
                //String s = String.valueOf(dataBaseHelperList.get(cursor.getPosition()).get_individual_product_names());
                //alTechSpecs.add(s);
            } while (cursor.moveToNext());
        }

        // return recent list
        return dataBaseHelperList;
    }

    public int updateSpecificOrderDataByID(DataBaseHelper dataBaseHelper, int KEY_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        //String column = "last_seen";
        ContentValues values = new ContentValues();
        //values.put(KEY_NAME, dataBaseHelper.getName());
        //values.put(KEY_NUMBER, dataBaseHelper.getPhoneNumber());
        values.put(KEY_PRODUCT_ID, dataBaseHelper.get_product_id_string());
        values.put(KEY_PRODUCT_NAME, dataBaseHelper.get_product_name());
        values.put(KEY_QUANTITY, dataBaseHelper.get_product_quantity_string());
        values.put(KEY_UNIT_PRICE, dataBaseHelper.get_unit_price_string());
        values.put(KEY_SUB_TOTAL, dataBaseHelper.get_sub_total_string());
        values.put(KEY_DELIVERY_DATE, dataBaseHelper.get_delivery_date());
        values.put(KEY_STATUS, dataBaseHelper.get_order_status());

        // updating row
        //return db.update(TABLE_RECENT, values, column + "last_seen", new String[] {String.valueOf(KEY_ID)});
        return db.update(TABLE_TEMP_PRODUCTS, values, "_id" + " = " + KEY_ID, null);
        //*//**//*ContentValues data=new ContentValues();
        //data.put("Field1","bob");
        //DB.update(Tablename, data, "_id=" + id, null);*//**//*
    }

    public void deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_RECENT, KEY_ID + " = ?", new String[] { String.valueOf(recent.getID()) });
        db.delete(TABLE_TEMP_PRODUCTS, KEY_ID + " = " + id, null);
        db.close();
    }

}
