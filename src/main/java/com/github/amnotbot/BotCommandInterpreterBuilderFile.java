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
package com.github.amnotbot;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import org.apache.commons.configuration.Configuration;

import com.github.amnotbot.config.BotConfiguration;

/**
 *
 * @author gpoppino
 */
public class BotCommandInterpreterBuilderFile
        extends BotCommandInterpreterBuilder
{
    private BotCommandInterpreter cmdInterpreter;

    @Override
    public void buildInterpreter()
    {
        this.cmdInterpreter = new BotCommandInterpreter();
    }

    @Override
    public BotCommandInterpreter getInterpreter()
    {
        return this.cmdInterpreter;
    }

    @Override
    public void loadCommands()
    {
        Configuration cmdConfig;
        cmdConfig = BotConfiguration.getCommandsConfig();

        Iterator it = cmdConfig.getKeys();
        while (it.hasNext()) {
            String cname, fpath;
            cname = (String) it.next();
            fpath = "com.github.amnotbot.cmd." + cname;
            Object o;
            try {
                Constructor constructor = Class.forName(fpath).getConstructor();
                o = constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                BotLogger.getDebugLogger().debug(e);
                continue;
            }

            if (o instanceof BotCommand) {
                String trigger = cmdConfig.getString(cname);
                BotCommand cmd = (BotCommand)o;
                this.cmdInterpreter.addListener(new BotCommandEvent(trigger), cmd);
            }
        }
    }
}
