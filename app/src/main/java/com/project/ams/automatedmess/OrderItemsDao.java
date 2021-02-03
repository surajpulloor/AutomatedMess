package com.project.ams.automatedmess;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface OrderItemsDao {
    @Query("SELECT * FROM OrderItem")
    LiveData<List<OrderItem>> getAll();

//    @Query("SELECT * FROM `order` WHERE uid IN (:userIds)")
//    List<OrderItem> loadAllByIds(int[] userIds);
//
//    @Query("SELECT * FROM `order` WHERE item_name LIKE :first AND "
//            + "last_name LIKE :last LIMIT 1")
//    OrderItem findByName(String first, String last);

    @Insert
    void insertAll(OrderItem... items);

    @Update
    void updateAll(OrderItem... items);

    @Delete
    void delete(OrderItem... items);

    // Used for deleting all the items in a particular order
    @Query("DELETE FROM OrderItem")
    void deleteAll();
}