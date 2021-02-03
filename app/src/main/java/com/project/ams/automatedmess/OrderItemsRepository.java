package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class OrderItemsRepository {

    private OrderItemsDao mOrderItemDao;
    private LiveData<List<OrderItem>> mAllItems;

    OrderItemsRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mOrderItemDao = db.orderDao();
        mAllItems = mOrderItemDao.getAll();
    }

    LiveData<List<OrderItem>> getAllItems() {
        return mAllItems;
    }


    public void insert (OrderItem word) {
        new insertAsyncTask(mOrderItemDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<OrderItem, Void, Void> {

        private OrderItemsDao mAsyncTaskDao;

        insertAsyncTask(OrderItemsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderItem... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    public void update (OrderItem item) {
        new updateAsyncTask(mOrderItemDao).execute(item);
    }

    private static class updateAsyncTask extends AsyncTask<OrderItem, Void, Void> {

        private OrderItemsDao mAsyncTaskDao;

        updateAsyncTask(OrderItemsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderItem... params) {
            mAsyncTaskDao.updateAll(params[0]);
            return null;
        }
    }

    public void delete (OrderItem item) {
        new deleteAsyncTask(mOrderItemDao).execute(item);
    }

    private static class deleteAsyncTask extends AsyncTask<OrderItem, Void, Void> {

        private OrderItemsDao mAsyncTaskDao;

        deleteAsyncTask(OrderItemsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderItem... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public void deleteAll () {
        new deleteAllAsyncTask(mOrderItemDao).execute();
    }

    private static class deleteAllAsyncTask extends AsyncTask<OrderItem, Void, Void> {

        private OrderItemsDao mAsyncTaskDao;

        deleteAllAsyncTask(OrderItemsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final OrderItem... params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }
}
