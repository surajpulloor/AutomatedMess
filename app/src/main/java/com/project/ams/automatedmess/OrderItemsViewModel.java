package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class OrderItemsViewModel extends AndroidViewModel {

    private OrderItemsRepository mRepository;

    private LiveData<List<OrderItem>> mAllItems;

    public OrderItemsViewModel(Application application) {
        super(application);
        mRepository = new OrderItemsRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    LiveData<List<OrderItem>> getAllItems() { return mAllItems; }

    public void insert(OrderItem item) { mRepository.insert(item); }

    public void update(OrderItem item) { mRepository.update(item); }

    public void delete(OrderItem item) { mRepository.delete(item); }

    public void deleteAll() { mRepository.deleteAll(); }
}
