package com.enonic.wem.api.vfs;

import java.util.Arrays;

import com.google.common.base.Joiner;

public abstract class AbstractFileName
{
    protected boolean absolute;

    protected String[] elements;

    protected final static String SEPARATOR = "/";

    protected String path;

    public AbstractFileName( final String[] elements, final boolean absolute )
    {
        this.elements = elements;
        this.absolute = absolute;
        this.path = Joiner.on( SEPARATOR ).join( Arrays.asList( elements ) );
    }


    public String getPathAsString()
    {
        return path;
    }

    public String[] getElements()
    {
        return elements;
    }

    public abstract String getLocalPath();

    public String getName()
    {
        return this.elements[this.elements.length - 1];
    }

    protected int size()
    {
        return this.elements.length;
    }

}
