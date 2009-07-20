package org.knix.amnotbot.cmd;

import java.sql.SQLException;
import java.util.Iterator;
import net.sf.jweather.Weather;
import net.sf.jweather.metar.Metar;
import net.sf.jweather.metar.SkyCondition;
import net.sf.jweather.metar.WeatherCondition;
import org.knix.amnotbot.BotLogger;
import org.knix.amnotbot.BotMessage;
import org.knix.amnotbot.cmd.db.BotDBFactory;
import org.knix.amnotbot.cmd.db.WeatherDAO;

/**
 *
 * @author gpoppino
 */
class WeatherImp
{
    private final BotMessage msg;
    private final String station;

    public WeatherImp(BotMessage msg)
    {
        this.msg = msg;
        this.station = msg.getText().toUpperCase().trim();
    }

    public void run()
    {
        try {
            if (this.station.isEmpty()) {
                this.showDefaultStation();
            } else if (this.station.startsWith("*")) {
                this.setAndShowDefaultStation();
            } else {
                this.showStation(this.station);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e);
        }
    }

    private void showDefaultStation() throws SQLException
    {
        WeatherDAO wDAO;
        wDAO = BotDBFactory.instance().createWeatherDAO();

        String _station = wDAO.getStation(this.msg.getConn().getHost(),
                this.msg.getUser().getNick());

        this.showStation(_station);
    }

    private void setAndShowDefaultStation() throws SQLException
    {
        WeatherDAO wDAO;
        wDAO = BotDBFactory.instance().createWeatherDAO();

        String _station = this.station.substring(1);
        final Metar metar = Weather.getMetar(_station);
        if (metar == null) {
            this.showHelp();
            return;
        }        
        wDAO.setStation(this.msg.getConn().getHost(),
                this.msg.getUser().getNick(), _station);

        this.showStation(metar);
    }

    private void showStation(String _station)
    {
        if (_station == null) {
            this.showHelp();
            return;
        }
        
        final Metar metar = Weather.getMetar(_station);
        if (metar == null) {
            this.showHelp();
            return;
        }
        this.showStation(metar);
    }

    private void showStation(Metar metar)
    {
        this.msg.getConn().doPrivmsg(this.msg.getTarget(),
                this.getReport(metar));
    }

    private void showHelp()
    {
        this.msg.getConn().doPrivmsg(this.msg.getTarget(), "Station IDs: " +
                    "http://www.weather.gov/tg/siteloc.shtml");
        return;
    }

    private String getReport(Metar metar)
    {
        String report = new String();

        report += metar.getStationID();
        report += " Temperature: " + metar.getTemperatureInCelsius() + "°C ";
        report += metar.getTemperatureInFahrenheit() + "°F";
        if (metar.getPressure() != null) {
            report += ", Pressure: " + metar.getPressure() + " hPa";
        }
        report += ", Wind: " + metar.getWindDirection() + "°";
        report += " at " + metar.getWindSpeedInMPH() + " MPH";

        Iterator it = metar.getSkyConditions().iterator();
        if (it.hasNext()) {
            report += ", Sky Conditions:";
        }
        while (it.hasNext()) {
            SkyCondition s = (SkyCondition) it.next();
            report += " " + s.getNaturalLanguageString();
        }

        it = metar.getWeatherConditions().iterator();
        if (it.hasNext()) {
            report += ", Weather Conditions:";
        }
        while (it.hasNext()) {
            WeatherCondition w = (WeatherCondition) it.next();
            report += " " + w.getNaturalLanguageString();
        }

        return report;
    }

}
