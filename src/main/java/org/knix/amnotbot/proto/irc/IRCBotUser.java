package org.knix.amnotbot.proto.irc;

import org.knix.amnotbot.BotUser;
import org.schwering.irc.lib.IRCUser;

/**
 *
 * @author gpoppino
 */
public class  IRCBotUser implements BotUser
{
    private IRCUser ircUser;

    public IRCBotUser(IRCUser user)
    {
        this.ircUser = user;
    }

    public IRCBotUser(String nick, String username, String host)
    {
        this.ircUser = new IRCUser(nick, username, host);
    }

    @Override
    public String getHost()
    {
        return this.ircUser.getHost();
    }

    @Override
    public String getNick()
    {
        return this.ircUser.getNick();
    }

    @Override
    public String getUsername()
    {
        return this.ircUser.getUsername();
    }

}