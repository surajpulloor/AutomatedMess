package com.project.ams.automatedmess;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {OrderItem.class, MessProviderProfile.class, AppUser.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract OrderItemsDao orderDao();
    public abstract MessProviderDao messProviderDao();
    public abstract AppUserDao appUserDao();

    private static AppDatabase INSTANCE;


    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "automated_mess")
                            .addCallback(sRoomDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;
    }

    // We will declare a room database callback which will be called whenever we want to perform
    // any db ops
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // Declare a inner class
    // We declare this class to interact with the SQLite database in an async way
    // Because database ops cannot be performed on the UI thread
    // it needs to be performed on its own thread

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final OrderItemsDao mOrderDao;
        private final MessProviderDao mMessDao;
        private final AppUserDao mAppUserDao;

        PopulateDbAsync(AppDatabase db) {
            mOrderDao = db.orderDao();
            mMessDao = db.messProviderDao();
            mAppUserDao = db.appUserDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
//            OrderItem order = new OrderItem();
//            order.setItemCategory("Kerala");
//            order.setItemName("Beef Fry");
//            order.setItemPrice(52.63);
//            order.setItemQuantity(3);
//            order.setItemType("non-veg");
//            mOrderDao.insertAll(order);
            return null;
        }
    }

}