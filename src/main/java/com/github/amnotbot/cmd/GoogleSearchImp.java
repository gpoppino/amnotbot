/*
 * Copyright (c) 2011 Geronimo Poppino <gresco@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.amnotbot.cmd;


import org.json.JSONObject;

import com.github.amnotbot.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gpoppino
 */
public class GoogleSearchImp
{

    private BotMessage msg;
    private GoogleResultOutputStrategy outputStrategy;

    public enum searchType {
        SPELLING_SEARCH, WEB_SEARCH
    }

    private searchType sType;

    public GoogleSearchImp(searchType sType,
            GoogleResultOutputStrategy outputStrategy,
            BotMessage msg)
    {
        this.msg = msg;
        this.sType = sType;
        this.outputStrategy = outputStrategy;
    }

    public void run()
    {
        try {
            GoogleSearch google = new GoogleSearch();

            String searchTerm = new String();
            switch (this.sType) {
                case WEB_SEARCH:
                    searchTerm = this.msg.getParams();
                    break;
                case SPELLING_SEARCH:
                    Pattern p =
                            Pattern.compile("([\\w]+)[\\s]?\\Q(sp?)\\E", Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(msg.getText().trim());
                    if (!m.find()) return;
                    searchTerm = m.group(1).trim();
                    break;
            }
            JSONObject answer = google.search(searchTerm);
            this.outputStrategy.showAnswer(this.msg, new GoogleResult(sType, answer));
        } catch (Exception e) {
            e.printStackTrace();
            BotLogger.getDebugLogger().debug(e.getMessage());
        }
    }
}
