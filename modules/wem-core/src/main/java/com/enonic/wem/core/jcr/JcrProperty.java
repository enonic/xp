package com.enonic.wem.core.jcr;

import java.util.Calendar;

public interface JcrProperty
{
    public void setValue( String value );

    public void setValue( String[] values );

    public void setValue( long value );

    public void setValue( double value );

    public void setValue( Calendar value );

    public void setValue( boolean value );

    public void setValue( JcrNode value );

    public String getString();

    public long getLong();

    public double getDouble();

    public Calendar getDate();

    public boolean getBoolean();

    public JcrNode getNode();
}
