package org.yearup.data.mysql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for DAO classes with a single connection to the database
 */

public abstract class MySqlDaoBase
{
    private final DataSource dataSource;

    public MySqlDaoBase(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}
