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
    private PreparedStatement mockPS;
    @Mock
    private ResultSet mockRS;

    @BeforeEach
    public void setup() {
        categoryDao = new MySqlCategoryDao(mockDataSource);
    }

    @Test
    void getAllCategories_returnList() throws SQLException {
        // arrange
        // simulates a table with multiple rows
        try {
            when(mockRS.next())
                    .thenReturn(true) // first row exists
                    .thenReturn(true) // second row exists
                    .thenReturn(true) // third row exists
                    .thenReturn(false); // no more rows
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // tests what the first row should return
        when(mockRS.getInt("category_id")).thenReturn(1);
        when(mockRS.getString("name")).thenReturn("Electronics");
        when(mockRS.getString("description")).thenReturn("Explore the latest gadgets and electronic devices.");

        // tests the second row
        when(mockRS.getInt("category_id")).thenReturn(2);
        when(mockRS.getString("name")).thenReturn("Fashion");
        when(mockRS.getString("description")).thenReturn("Discover trendy clothing and accessories for men and women.");

        // test the third row
        when(mockRS.getInt("category_id")).thenReturn(3);
        when(mockRS.getString("name")).thenReturn("Home & Kitchen");
        when(mockRS.getString("description")).thenReturn("Find everything you need to decorate and equip your home.");

        // act
        List<Category> categories = categoryDao.getAllCategories();


        // assert
        // verify that the correct methods were called on mocks
        verify(mockDataSource, times(1)).getConnection(); // getConnection() called once
        verify(mockConnection, times(1)).prepareStatement("SELECT * FROM categories"); // The specific query was prepared
        verify(mockPS, times(1)).executeQuery(); // executeQuery() was called once
        verify(mockRS, times(3)).next(); // next() was called 3 times (true, true, false)
        verify(mockRS, times(2)).getInt("category_id"); // getInt("category_id") called twice (for each row)
        verify(mockRS, times(2)).getString("name");
        verify(mockRS, times(2)).getString("description");

        assertNotNull(categories);
        assertEquals(2, categories.size());

        assertEquals(new Category(1, "Electronics", "Gadgets and devices"), categories.get(0));
        assertEquals(new Category(2, "Books", "Fiction and non-fiction"), categories.get(1));
    }




// skipping asserting size of categories since that can change depending on admin

}