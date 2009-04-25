package org.knix.amnotbot.command.utils;

import org.knix.amnotbot.*;

public class CmdCommaSeparatedOption implements CmdOption {
	
	private String name;
	private String arg0;
	
	public CmdCommaSeparatedOption(String name) {
		this.name = name;
		this.arg0 = null;
	}
	
	public void buildArgs(String msg) 
	{
		int index;
		String arg = new String();		
		
		this.arg0 = null;
		
		if (msg == null)
			return;
				
		index = msg.indexOf(this.name + ":");
		BotLogger.getDebugLogger().debug("1. index of " + index + " " + msg);
		if (index >= 0) {		
			index += this.name.length() + 1;
			BotLogger.getDebugLogger().debug("2. index of " + index + " " + msg.substring(index, msg.length()) + " length " + msg.length());
		
			if (index < msg.length()) {				
				for (int i = index; i < msg.length(); ++i) {
					char c;
			
					c = msg.charAt(i);
					if (c == ' ' || c == '\n' || c == '\0')
						break;
			
					arg += c;			
				}				
		
				BotLogger.getDebugLogger().debug("arg = " + arg);
				if (arg.length() > 0)
					this.arg0 = arg.trim();
			}
		}
	}

	public String getName() 
	{		
		return name;
	}

	public String stringValue(String sep, String joinChar) 
	{
		String [] keywords;
		String value = "";
		
		if (this.arg0 != null) {
			String arg = this.arg0;
			
			BotLogger.getDebugLogger().debug("arg " + arg);
			
			keywords = arg.split(",");
			for (int j = 0; j < keywords.length; ++j) {
				String word = keywords[j];
				word = word.trim();		
				word = word.replaceAll("\\s", joinChar);						
				value += word + sep;
			}
		}
		
		BotLogger.getDebugLogger().debug("value " + value);
		
		return value.toLowerCase();
	}

	public String stringValue(String sep) 
	{
		String [] keywords;
		String value = "";
		
		if (this.arg0 != null) {
			String arg = this.arg0;
			keywords = arg.split(",");
			for (int j = 0; j < keywords.length; ++j) {
				String word = keywords[j];
				word = word.trim();										
				value += word + sep;
			}
		} else {
			return null;
		}
		
		return value.toLowerCase();
	}

	public String stringValue() 
	{
		if (this.hasValue())
		    return this.arg0.toLowerCase();
		else
		    return this.arg0;
	}

	public boolean hasValue() 
	{
		if (this.arg0 != null)
			return true;
		
		return false;
	}		
}