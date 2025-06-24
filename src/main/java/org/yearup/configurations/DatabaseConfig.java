package org.yearup.configurations;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig
{
    /**
     * Spring Configuration class for connecting to database which initializes the BasicDataSource
     * @param url is the url of the website that uses the datasource
     * @param username is the username (user/admin)
     * @param password is the password
     * @return the datasource that is access through the username and password, url is the location
     */

    private BasicDataSource basicDataSource;

    @Bean
    public BasicDataSource dataSource()
    {
        return basicDataSource;
    }

    @Autowired
    public DatabaseConfig(@Value("${datasource.url}") String url,
                          @Value("${datasource.username}") String username,
                          @Value("${datasource.password}") String password)
    {
        basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
    }




}