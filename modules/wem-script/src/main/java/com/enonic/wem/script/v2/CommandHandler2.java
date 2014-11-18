package com.enonic.wem.script.v2;

public interface CommandHandler2<I, O>
{
    public String getName();

    public I createInputBean();

    public O execute( I input );
}
