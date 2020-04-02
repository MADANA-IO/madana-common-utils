package com.madana.common.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

public class ApplicationOutputStream extends PrintStream {
	String applicationName;

    public ApplicationOutputStream(String applicationName, OutputStream out) {
        super(out);
        this.applicationName=applicationName;
    }

    public ApplicationOutputStream(String applicationName, PrintStream out, boolean b) 
    {
        super(out,b);
        this.applicationName=applicationName;
	}

	@Override
    public void println(String string) {
        Date date = new Date();
        String text ="["+applicationName+"][" + date.toString() + "] " + string;
        super.println(text);

    }
}