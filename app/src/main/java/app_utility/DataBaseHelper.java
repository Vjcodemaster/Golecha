package app_utility;


public class DataBaseHelper {

    //private variables
    private int _id;
    //private String _phone_number;
    private int _odoo_id;
    private String _sales_order_id;
    private int _sales_order_line_id;
    private int _product_id;
    private String _product_name;
    private int _product_quantity;
    private int _unit_price;
    private int _sub_total;

    private String _product_id_string;
    private String _product_quantity_string;
    private String _unit_price_string;
    private String _sub_total_string;

    private String _order_status;

    private String _delivery_date;


    // Empty constructor
    public DataBaseHelper() {

    }

    public DataBaseHelper(int _product_id, String _product_name, String _unit_price_string){
        this._product_id = _product_id;
        this._product_name = _product_name;
        this._unit_price_string = _unit_price_string;
    }


    public DataBaseHelper(String _product_id_string, String _product_name, String _product_quantity_string,
                          String _unit_price_string, String _sub_total_string, String _delivery_date,
                          String _order_status){
        this._product_id_string = _product_id_string;
        this._product_name = _product_name;
        this._product_quantity_string = _product_quantity_string;
        this._unit_price_string = _unit_price_string;
        this._sub_total_string = _sub_total_string;
        this._delivery_date = _delivery_date;
        this._order_status = _order_status;
    }

    //temporary db
    /*public DataBaseHelper(int _product_id, String _product_name, int _product_quantity, int _unit_price, int _sub_total){
        this._product_id = _product_id;
        this._product_name = _product_name;
        this._product_quantity = _product_quantity;
        this._unit_price = _unit_price;
        this._sub_total = _sub_total;
    }*/

    public DataBaseHelper(int _odoo_id, String _sales_order_id, String _product_id_string,
                          String _product_name, String _product_quantity_string, String _unit_price_string, String _sub_total_string,
                          String _order_status){
        this._odoo_id = _odoo_id;
        this._sales_order_id = _sales_order_id;
        this._product_id_string = _product_id_string;
        this._product_name = _product_name;
        this._product_quantity_string = _product_quantity_string;
        this._unit_price_string = _unit_price_string;
        this._sub_total_string = _sub_total_string;
        this._order_status = _order_status;
    }

    // getting ID
    public int get_id() {
        return this._id;
    }

    // setting id
    public void set_id(int id) {
        this._id = id;
    }

    public int get_odoo_id() {
        return this._odoo_id;
    }

    public void set_odoo_id(int odoo_id) {
        this._odoo_id = odoo_id;
    }

    public String get_sales_order_id() {
        return this._sales_order_id;
    }

    public void set_sales_order_id(String sales_order_id) {
        this._sales_order_id = sales_order_id;
    }

    public int get_sales_order_line_id() {
        return this._sales_order_line_id;
    }

    public void set_sales_order_line_id(int sales_order_line_id) {
        this._sales_order_line_id = sales_order_line_id;
    }

    public int get_product_id() {
        return this._product_id;
    }

    public void set_product_id(int product_id) {
        this._product_id = product_id;
    }

    public String get_product_name() {
        return this._product_name;
    }

    public void set_product_name(String product_name) {
        this._product_name = product_name;
    }

    public int get_product_quantity() {
        return this._product_quantity;
    }

    public void set_product_quantity(int product_quantity) {
        this._product_quantity = product_quantity;
    }

    public int get_unit_price() {
        return this._unit_price;
    }

    public void set_unit_price(int unit_price) {
        this._unit_price = unit_price;
    }

    public int get_sub_total() {
        return this._sub_total;
    }

    public void set_sub_total(int sub_total) {
        this._sub_total = sub_total;
    }

    public String get_product_id_string() {
        return this._product_id_string;
    }

    public void set_product_id_string(String product_id_string) {
        this._product_id_string = product_id_string;
    }

    public String get_product_quantity_string() {
        return this._product_quantity_string;
    }

    public void set_product_quantity_string(String product_quantity_string) {
        this._product_quantity_string = product_quantity_string;
    }

    public String get_unit_price_string() {
        return this._unit_price_string;
    }

    public void set_unit_price_string(String unit_price_string) {
        this._unit_price_string = unit_price_string;
    }

    public String get_sub_total_string() {
        return this._sub_total_string;
    }

    public void set_sub_total_string(String sub_total_string) {
        this._sub_total_string = sub_total_string;
    }

    public String get_order_status() {
        return this._order_status;
    }

    public void set_order_status(String order_status) {
        this._order_status = order_status;
    }

    public String get_delivery_date() {
        return this._delivery_date;
    }

    public void set_delivery_date(String delivery_date) {
        this._delivery_date = delivery_date;
    }
}
