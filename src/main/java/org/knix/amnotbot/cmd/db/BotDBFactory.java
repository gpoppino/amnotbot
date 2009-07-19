package org.knix.amnotbot.cmd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.knix.amnotbot.BotLogger;
import org.knix.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotDBFactory
{
    String backend, driver;
    Properties properties;
    static BotDBFactory _instance = null;
    WeatherDAO weatherDAO = null;

    public static BotDBFactory instance()
    {
        if (_instance == null) {
            _instance = new BotDBFactory();
        }
        return _instance;
    }

    protected BotDBFactory()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            BotLogger.getDebugLogger().debug(e);
        }
        
        this.backend = BotConfiguration.getConfig().getString("backend");

        this.properties = new Properties();
        if (this.backend.equals("hsqldb")) {
            this.driver = this.backend + ":file";
            this.properties.setProperty("shutdown", "true");
        } else {
            this.driver = this.backend;
        }
    }

    public Connection getConnection(String db) throws SQLException
    {
        Connection connection = null;

        connection = DriverManager.getConnection(
                "jdbc:" + this.driver + ":" + db, this.properties);

        return connection;
    }

    public QuoteDAO createQuoteDAO(String db)           
    {
        QuoteDAO result = null;
        Class quoteClass = null;
        
        try {            
            String className = this.backend.substring(0, 1).toUpperCase() +
                    this.backend.substring(1);
            quoteClass = Class.forName("org.knix.amnotbot.cmd.db.backend." +
                    className + "QuoteDAO");

            Class[] types = { java.sql.Connection.class };      
            java.lang.reflect.Constructor constructor =
                    quoteClass.getConstructor(types);

            Object[] params = { this.getConnection(db) };          
            result = (QuoteDAO) constructor.newInstance( params );
            
        } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return result;
    }

    public WordCounterDAO createWordCounterDAO(String db)           
    {
        WordCounterDAO wCounterDAO = null;

        try {
            return (WordCounterDAO) new JDBCWordCounterDAO(db);
        } catch (SQLException e) {
            BotLogger.getDebugLogger().debug(e);
        }

        return wCounterDAO;
    }

    public WeatherDAO createWeatherDAO()
    {
        if (this.weatherDAO != null) return this.weatherDAO;

        Class weatherClass = null;
        String db = BotLogger.BOT_HOME + "/" + "weather.db";
        
        try {
            String className = this.backend.substring(0, 1).toUpperCase() +
                    this.backend.substring(1);
            weatherClass = Class.forName("org.knix.amnotbot.cmd.db.backend." +
                    className + "WeatherDAO");

            Class[] types = { java.lang.String.class };
            java.lang.reflect.Constructor constructor =
                    weatherClass.getConstructor(types);
            Object[] params = { db };
            
            this.weatherDAO = (WeatherDAO) constructor.newInstance( params );
            if (!this.weatherDAO.stationDBExists()) {
                this.weatherDAO.createStationDB();
            }

        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
        return this.weatherDAO;
    }
}
