package com.github.amnotbot.cmd;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import com.github.amnotbot.BotCommand;
import com.github.amnotbot.BotMessage;
import com.github.amnotbot.cmd.utils.URLGrabber;
import com.github.amnotbot.cmd.utils.Utf8ResourceBundle;
import com.github.amnotbot.cmd.utils.WebPageInfoProxy;
import com.github.amnotbot.config.BotConfiguration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gpoppino
 */
public class WebPageInfoCommand implements BotCommand
{

    public void execute(BotMessage message)
    {
        URLGrabber urlGrabber = new URLGrabber(message.getText());
        
        WebPageInfoProxy webPageInfo;
        webPageInfo = new WebPageInfoProxy(urlGrabber.getURL());
        String title = webPageInfo.getTitle();
        if (title != null) {
            message.getConn().doPrivmsg(message.getTarget(),
                    "[ " + title + " ]");
        }
    }

    public String help()
    {
        Locale currentLocale;
        ResourceBundle helpMessage;

        currentLocale = new Locale(
                BotConfiguration.getConfig().getString("language"),
                BotConfiguration.getConfig().getString("country"));
        helpMessage = Utf8ResourceBundle.getBundle("WebPageInfoCommandBundle",
                currentLocale);

        Object[] messageArguments = {
            helpMessage.getString("short_description"),
            helpMessage.getString("long_description"),
        };

        MessageFormat formatter = new MessageFormat("");
        formatter.setLocale(currentLocale);
        formatter.applyPattern(helpMessage.getString("template"));

        String output = formatter.format(messageArguments);
        return output;
    }
}
