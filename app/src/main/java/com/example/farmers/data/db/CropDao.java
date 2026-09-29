package com.example.farmers.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.farmers.data.model.Crop;
import java.util.List;

@Dao
public interface CropDao {

    @Insert
    long insert(Crop crop);

    @Update
    void update(Crop crop);

    @Delete
    void delete(Crop crop);

    @Query("SELECT * FROM crops ORDER BY createdAt DESC")
    LiveData<List<Crop>> getAllCrops();

    @Query("SELECT * FROM crops WHERE status = 'GROWING' ORDER BY harvestDate ASC")
    LiveData<List<Crop>> getGrowingCrops();

    @Query("SELECT * FROM crops WHERE id = :id")
    Crop getCropById(int id);

    @Query("DELETE FROM crops WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(*) FROM crops WHERE status = 'GROWING'")
    int getGrowingCount();
}
