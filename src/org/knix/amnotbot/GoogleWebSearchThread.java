package org.knix.amnotbot;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.htmlparser.util.ParserUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author gpoppino
 */
public class GoogleWebSearchThread extends Thread
{
    private BotConnection con;
    private String query;
    private String chan;
    private String nick;

    public GoogleWebSearchThread(BotConnection con,
            String chan,
            String nick,
            String query)
    {
        this.con = con;
        this.chan = chan;
        this.nick = nick;
        this.query = query;

        start();
    }

    public void run()
    {
        try {          
            GoogleSearch google = new GoogleSearch();
            
            JSONObject answer;
            answer = google.search(GoogleSearch.searchType.WEB_SEARCH,
                        this.query);

            this.showAnswer(answer);
         } catch (Exception e) {
            BotLogger.getDebugLogger().debug(e.getMessage());
         }
    }

    private void showAnswer(JSONObject answer) 
            throws JSONException, UnsupportedEncodingException
    {
        JSONObject data = answer.getJSONObject("responseData");
        JSONObject result = data.getJSONArray("results").getJSONObject(0);

        String title = result.optString("titleNoFormatting");
        this.con.doPrivmsg(this.chan, title);

        String url = URLDecoder.decode(result.optString("url"), "UTF-8");
        this.con.doPrivmsg(this.chan, url);

        String snippet = result.optString("content");
        this.con.doPrivmsg(this.chan,
                ParserUtils.trimAllTags(snippet, false));
    }
}