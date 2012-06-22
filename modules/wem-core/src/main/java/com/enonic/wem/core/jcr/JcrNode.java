package com.enonic.wem.core.jcr;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Property;

public interface JcrNode
{
    String getIdentifier();

    String getName();

    String getPath();

    JcrNode getParent();

    boolean hasProperty( String relPath );

    Property getProperty( String relPath );

    String getPropertyString( String relPath );

    Boolean getPropertyBoolean( String relPath );

    long getPropertyLong( String relPath );

    double getPropertyDouble( String relPath );

    Date getPropertyDate( String relPath );

    Calendar getPropertyCalendar( String relPath );


    void setPropertyString( String relPath, String value );

    void setPropertyBoolean( String relPath, boolean value );

    void setPropertyLong( String relPath, long value );

    void setPropertyDouble( String relPath, double value );

    void setPropertyDate( String relPath, Date value );

    void setPropertyCalendar( String relPath, Calendar value );

    void setPropertyBinary( String relPath, byte[] value );

    void setPropertyReference( String name, JcrNode value );

    JcrNode getNode( String relPath );

    boolean hasNode( String relPath );

    JcrNode addNode( String relPath );

    JcrNode addNode( String relPath, String primaryNodeTypeName );

    JcrNodeIterator getNodes( String namePattern );

    JcrPropertyIterator getReferences( String name );

    JcrPropertyIterator getReferences();

}
