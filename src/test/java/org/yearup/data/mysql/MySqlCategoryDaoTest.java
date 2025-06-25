package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.data.mysql.BaseDaoTestClass;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class) //using Mockito for creating mock database to run tests
class MySqlCategoryDaoTest {

    @InjectMocks
    private MySqlCategoryDao categoryDao;
    @Mock
    private DataSource mockDataSource;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPs;
    @Mock
    private ResultSet mockRs;

    @BeforeEach
    public void setup() throws SQLException {
//        categoryDao = new MySqlCategoryDao(mockDataSource);
        // Define the behavior of the mocks when they are called by categoryDao:
        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
        when(mockPs.executeQuery()).thenReturn(mockRs);
    }

    /**
     * This unit test only works when there are no additions to the category table.
     * Admin must modify code in order to run additions
     */

    @Test
    void getAllCategories_returnList() throws SQLException {
        // arrange
        // simulates a table with multiple rows (not including auto generated id)
        try {
            when(mockRs.next())
                    .thenReturn(true) // first row exists
                    .thenReturn(true) // second row exists
                    .thenReturn(true) // third row exists
                    .thenReturn(false); // no more rows
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        // tests what the first row should return
        when(mockRs.getInt("category_id")).thenReturn(1).thenReturn(2).thenReturn(3);
        when(mockRs.getString("name")).thenReturn("Electronics").thenReturn("Fashion").thenReturn("Home & Kitchen");
        when(mockRs.getString("description")).thenReturn("Explore the latest gadgets and electronic devices.").thenReturn("Discover trendy clothing and accessories for men and women.").thenReturn("Find everything you need to decorate and equip your home.");

//        // tests the second row
//        when(mockRs.getInt("category_id")).thenReturn(2);
//        when(mockRs.getString("name")).thenReturn("Fashion");
//        when(mockRs.getString("description")).thenReturn("Discover trendy clothing and accessories for men and women.");
//
//        // test the third row
//        when(mockRs.getInt("category_id")).thenReturn(3);
//        when(mockRs.getString("name")).thenReturn("Home & Kitchen");
//        when(mockRs.getString("description")).thenReturn("Find everything you need to decorate and equip your home.");

        // act
        List<Category> categories = categoryDao.getAllCategories();


        // assert
        // verify that the correct methods were called on mocks
        verify(mockDataSource, times(1)).getConnection(); // getConnection() called once
        verify(mockConnection, times(1)).prepareStatement("SELECT * FROM categories"); // The specific query was prepared
        verify(mockPs, times(1)).executeQuery(); // executeQuery() was called once
        verify(mockRs, times(4)).next(); // next() was called 3 times (true, true, false) originally
        verify(mockRs, times(3)).getInt("category_id"); // getInt("category_id") called twice (for each row)
        verify(mockRs, times(3)).getString("name");
        verify(mockRs, times(3)).getString("description");

        assertNotNull(categories);
        assertEquals(3, categories.size());

        assertEquals(new Category(1, "Electronics", "Explore the latest gadgets and electronic devices."), categories.get(0));
        assertEquals(new Category(2, "Fashion", "Discover trendy clothing and accessories for men and women."), categories.get(1));
        assertEquals(new Category(3, "Home & Kitchen", "Find everything you need to decorate and equip your home."), categories.get(2));
    }


// skipping asserting size of categories since that can change depending on admin

}