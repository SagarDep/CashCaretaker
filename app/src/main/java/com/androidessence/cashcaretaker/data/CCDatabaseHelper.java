package com.androidessence.cashcaretaker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adammcneilly on 10/30/15.
 */
public class CCDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cashcaretaker.db";
    private static final int DATABASE_VERSION = 1;

    public CCDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        buildAccountTable(db);
        buildCategoryTable(db);
        buildTransactionTable(db);
        addUpdateBalanceForWithdrawalTrigger(db);
        addUpdateBalanceForDepositTrigger(db);
        addTransactionCascadeDeleteTrigger(db);
        addUpdateBalanceForWithdrawalDeleteTrigger(db);
        addUpdateBalanceForDepositDeleteTrigger(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void buildAccountTable(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + CCContract.AccountEntry.TABLE_NAME + " (" +
                        CCContract.AccountEntry._ID + " INTEGER PRIMARY KEY, " +
                        CCContract.AccountEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                        CCContract.AccountEntry.COLUMN_BALANCE + " REAL NOT NULL);"
        );
    }

    private void buildCategoryTable(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + CCContract.CategoryEntry.TABLE_NAME + " (" +
                        CCContract.CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                        CCContract.CategoryEntry.COLUMN_DESCRIPTION + " TEXT UNIQUE NOT NULL);"
        );

        db.execSQL(
                "INSERT INTO " + CCContract.CategoryEntry.TABLE_NAME + " (" + CCContract.CategoryEntry.COLUMN_DESCRIPTION + ") " +
                        "VALUES " +
                        "('ATM'), ('Automotive'), ('Bills'), ('Food'), ('Gasoline'), ('Home'), ('Paycheck'), ('None');"
        );
    }

    private void buildTransactionTable(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + CCContract.TransactionEntry.TABLE_NAME + " (" +
                        CCContract.TransactionEntry._ID + " INTEGER PRIMARY KEY, " +
                        CCContract.TransactionEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        CCContract.TransactionEntry.COLUMN_AMOUNT + " REAL NOT NULL, " +
                        CCContract.TransactionEntry.COLUMN_NOTES + " TEXT DEFAULT '', " +
                        CCContract.TransactionEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                        CCContract.TransactionEntry.COLUMN_CATEGORY + " INTEGER NOT NULL, " +
                        CCContract.TransactionEntry.COLUMN_WITHDRAWAL + " INTEGER NOT NULL, " +
                        CCContract.TransactionEntry.COLUMN_ACCOUNT +  " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + CCContract.TransactionEntry.COLUMN_CATEGORY + ") " +
                        "REFERENCES " + CCContract.CategoryEntry.TABLE_NAME + " (" + CCContract.CategoryEntry._ID + "), " +
                        "FOREIGN KEY (" + CCContract.TransactionEntry.COLUMN_ACCOUNT + ") " +
                        "REFERENCES " + CCContract.AccountEntry.TABLE_NAME + " (" + CCContract.AccountEntry._ID + " ));"
        );
    }

    private void addUpdateBalanceForWithdrawalTrigger(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TRIGGER update_balance_for_withdrawal " +
                        "AFTER INSERT ON " + CCContract.TransactionEntry.TABLE_NAME + " " +
                        "WHEN new." + CCContract.TransactionEntry.COLUMN_WITHDRAWAL + " " +
                        "BEGIN " +
                        "UPDATE " + CCContract.AccountEntry.TABLE_NAME + " " +
                        "SET " + CCContract.AccountEntry.COLUMN_BALANCE + " = " + CCContract.AccountEntry.COLUMN_BALANCE + " - new." + CCContract.TransactionEntry.COLUMN_AMOUNT + " " +
                        "WHERE " + CCContract.AccountEntry._ID + " = new." + CCContract.TransactionEntry.COLUMN_ACCOUNT + "; END;"
        );
    }

    private void addUpdateBalanceForDepositTrigger(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TRIGGER update_balance_for_deposit " +
                        "AFTER INSERT ON " + CCContract.TransactionEntry.TABLE_NAME + " " +
                        "WHEN NOT new." + CCContract.TransactionEntry.COLUMN_WITHDRAWAL + " " +
                        "BEGIN " +
                        "UPDATE " + CCContract.AccountEntry.TABLE_NAME + " " +
                        "SET " + CCContract.AccountEntry.COLUMN_BALANCE + " = " + CCContract.AccountEntry.COLUMN_BALANCE + " + new." + CCContract.TransactionEntry.COLUMN_AMOUNT + " " +
                        "WHERE " + CCContract.AccountEntry._ID + " = new." + CCContract.TransactionEntry.COLUMN_ACCOUNT + "; END;"
        );
    }

    private void addTransactionCascadeDeleteTrigger(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TRIGGER delete_transactions_for_account " +
                        "BEFORE DELETE ON " + CCContract.AccountEntry.TABLE_NAME + " " +
                        "FOR EACH ROW BEGIN " +
                        "DELETE FROM " + CCContract.TransactionEntry.TABLE_NAME + " " +
                        "WHERE " + CCContract.TransactionEntry.COLUMN_ACCOUNT + " = old." + CCContract.AccountEntry._ID + "; END;"
        );
    }

    private void addUpdateBalanceForWithdrawalDeleteTrigger(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TRIGGER update_balance_for_withdrawal_delete " +
                        "AFTER DELETE ON " + CCContract.TransactionEntry.TABLE_NAME + " " +
                        "WHEN old." + CCContract.TransactionEntry.COLUMN_WITHDRAWAL + " " +
                        "BEGIN " +
                        "UPDATE " + CCContract.AccountEntry.TABLE_NAME + " " +
                        "SET " + CCContract.AccountEntry.COLUMN_BALANCE + " = " + CCContract.AccountEntry.COLUMN_BALANCE + " + old." + CCContract.TransactionEntry.COLUMN_AMOUNT + " " +
                        "WHERE " + CCContract.AccountEntry._ID + " = old." + CCContract.TransactionEntry.COLUMN_ACCOUNT + "; END;"
        );
    }

    private void addUpdateBalanceForDepositDeleteTrigger(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TRIGGER update_balance_for_deposit_delete " +
                        "AFTER DELETE ON " + CCContract.TransactionEntry.TABLE_NAME + " " +
                        "WHEN NOT old." + CCContract.TransactionEntry.COLUMN_WITHDRAWAL + " " +
                        "BEGIN " +
                        "UPDATE " + CCContract.AccountEntry.TABLE_NAME + " " +
                        "SET " + CCContract.AccountEntry.COLUMN_BALANCE + " = " + CCContract.AccountEntry.COLUMN_BALANCE + " - old." + CCContract.TransactionEntry.COLUMN_AMOUNT + " " +
                        "WHERE " + CCContract.AccountEntry._ID + " = old." + CCContract.TransactionEntry.COLUMN_ACCOUNT + "; END;"
        );
    }
}
