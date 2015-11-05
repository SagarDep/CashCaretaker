package com.androidessence.cashcaretaker.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by adammcneilly on 10/30/15.
 */
public class CCContract {
    // Content authority is a name for the entire content provider
    // similar to a domain and its website. This string is guaranteed to be unqiue.
    public static final String CONTENT_AUTHORITY = "com.androidessence.cashcaretaker";

    // Use the content authority to provide the base
    // of all URIs
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths for URIs
    public static final String PATH_ACCOUNT = "account";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_TRANSACTION = "transaction";

    public static final class AccountEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_ACCOUNT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_ACCOUNT;

        public static final String TABLE_NAME = "accountTable";
        public static final String COLUMN_NAME = "accountName";
        public static final String COLUMN_BALANCE = "accountBalance";

        public static Uri buildAccountUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CATEGORY;

        public static final String TABLE_NAME = "categoryTable";
        public static final String COLUMN_DESCRIPTION = "categoryDescription";

        public static Uri buildCategoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class TransactionEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_TRANSACTION;

        public static final String TABLE_NAME = "transactionTable";
        public static final String COLUMN_DESCRIPTION = "transactionDescription";
        public static final String COLUMN_AMOUNT = "transactionAmount";
        public static final String COLUMN_NOTES = "transactionNotes";
        public static final String COLUMN_DATE = "transactionDate";
        public static final String COLUMN_CATEGORY = "transactionCategory";
        public static final String COLUMN_WITHDRAWAL = "transactionWithdrawal";
        public static final String COLUMN_ACCOUNT = "transactionAccount";

        public static Uri buildTransactionUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTransactionsForAccountUri(long account){
            Uri accountUri = CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNT).build();
            return ContentUris.withAppendedId(accountUri, account);
        }
    }
}
