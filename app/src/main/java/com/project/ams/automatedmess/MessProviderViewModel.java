package com.project.ams.automatedmess;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MessProviderViewModel extends AndroidViewModel {
    private MessProviderRepository mRepository;

    private LiveData<List<MessProviderProfile>> mAllItems;

    public MessProviderViewModel(Application application) {
        super(application);
        mRepository = new MessProviderRepository(application);
        mAllItems = mRepository.getAllItems();
    }

    LiveData<List<MessProviderProfile>> getAllItems() { return mAllItems; }

    public void insert(MessProviderProfile messProvider) { mRepository.insert(messProvider); }

    public void deleteAll() { mRepository.deleteAll(); }
}
