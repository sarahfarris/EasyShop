package org.yearup.configuration;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;



@Configuration
public class TestDatabaseConfig
{
    private final String serverUrl;
    private final String testDb;
    private final String username;
    private final String password;
    private final String driverClassName; // <--- NEW: Field for driver class name

    @Autowired
    public TestDatabaseConfig(@Value("${datasource.url}") String serverUrl,
                              @Value("${datasource.username}") String username,
                              @Value("${datasource.password}") String password,
                              @Value("${datasource.testdb}") String testDb,
                              @Value("${datasource.driver-class-name}") String driverClassName) // <--- NEW
    {
        this.serverUrl = serverUrl;
        this.testDb = testDb;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName; // NEW
    }

    @PostConstruct
    public void setup() {

        // added this to try to fix unit testing
        // -- begin code block --
        // --- Added for explicit driver loading ---
        try {
            Class.forName(driverClassName); // Use injected driverClassName
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found in classpath: " + driverClassName, e);
        }
        // --- End driver loading ---
        // -- end of code block --

        String connectionUrlForSetup = serverUrl + "mysql"; // trying to fix product unit test

        try(Connection connection = DriverManager.getConnection(serverUrl + "mysql", username, password);
            Statement statement = connection.createStatement();
        )
        {
            System.out.println("TestDatabaseConfig: Attempting to connect to: " + connectionUrlForSetup); // Debug print
            System.out.println("TestDatabaseConfig: Attempting to DROP and CREATE database '" + testDb + "'");

            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
            statement.execute("CREATE DATABASE " + testDb + ";");

            System.out.println("TestDatabaseConfig: Database '" + testDb + "' successfully dropped and created.");
        }
        catch (SQLException e ){
            System.err.println("TestDatabaseConfig: error during @PostConstruct database setup: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test database in @PostConstruct" + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void cleanup() {
        // --- Added for explicit driver loading in cleanup ---
        try {
            Class.forName(driverClassName); // Use injected driverClassName
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found during cleanup: " + driverClassName + " - " + e.getMessage());
            // Don't throw here, as cleanup should ideally complete
            return;
        }

        String connectionUrlForSetup = serverUrl + "mysql"; // trying to fix product unit test
        // --- End driver loading ---
        try(Connection connection = DriverManager.getConnection(connectionUrlForSetup + "/sys", username, password);
            Statement statement = connection.createStatement();
        )
        {
            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
        }
        catch (SQLException ignored){}

    }


    @Bean
    public DataSource dataSource() throws SQLException, IOException
    {
        // --- Added for explicit driver loading ---
        try {
            Class.forName(driverClassName); // Use injected driverClassName
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found for DataSource initialization!", e);
        }
        // --- End driver loading ---


        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Explicitly set for the DataSource too
        dataSource.setUrl(String.format("%s%s", serverUrl, testDb)); //added a slash
//        dataSource.setUrl(String.format("%s/%s", serverUrl, testDb)); originally in the code, trying it without the slash
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);

        //  --- adding this code block to try to solve product test not working ---

        try (Connection tempConnection = DriverManager.getConnection(serverUrl + testDb, username, password)) {
            ScriptRunner runner = new ScriptRunner(tempConnection);
            runner.setAutoCommit(true); // ScriptRunner operates best with auto-commit on
            runner.setStopOnError(true); // Stop if any script error occurs
            runner.setDelimiter(";"); // Standard delimiter for MySQL scripts

            // Use ClassPathResource.getURL().getPath() or getFile().getAbsolutePath()
            // Make sure "test-data.sql" is directly in src/test/resources
            Reader reader = new BufferedReader(new FileReader(new ClassPathResource("test-data.sql").getFile()));
            System.out.println("TestDatabaseConfig: Running test-data.sql from: " + new ClassPathResource("test-data.sql").getFile().getAbsolutePath());
            runner.runScript(reader);
            // No need to commit here if runner.setAutoCommit(true)
        } catch (Exception e) {
            System.err.println("Error running test-data.sql script: " + e.getMessage());
            throw new RuntimeException("Failed to run test-data.sql", e);
        }

        // --- end of code block ---

        // -- code block part of the original code
//        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
//        Reader reader = new BufferedReader(new FileReader((new ClassPathResource("test-data.sql")).getFile().getAbsolutePath()));
//        runner.runScript(reader);
//        dataSource.getConnection().commit();
        // -- end of code block --
        return dataSource;
    }
}
