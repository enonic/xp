package com.enonic.wem.core.jcr;

import java.util.Date;

import org.joda.time.DateTime;

public interface JcrProperty
{
    void setValue( String value );

    void setValue( String[] values );

    void setValue( long value );

    void setValue( double value );

    void setValue( Date value );

    void setValue( DateTime value );

    void setValue( boolean value );

    void setValue( JcrNode value );

    void setValue( byte[] value );

    String getString();

    long getLong();

    double getDouble();

    Date getDate();

    DateTime getDateTime();

    boolean getBoolean();

    JcrNode getNode();

    byte[] getBinary();

    JcrNode getParent();
}
