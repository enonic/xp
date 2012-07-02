package com.enonic.wem.core.jcr;

import java.util.Date;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.joda.time.DateTime;

public interface JcrSession
{
    Session getRealSession();

    JcrRepository getRepository();

    void login();

    void logout();

    void save();

    JcrNode getNodeByIdentifier( String id );

    JcrNode getRootNode();

    JcrNode getNode( String absPath );

    JcrNode getOrCreateNode( String absPath );

    JcrNode getNode( Node node );

    boolean nodeExists( String absPath );

    void removeItem( String absPath );

    boolean propertyExists( String absPath );

    Property getProperty( String absPath );

    String getPropertyString( String absPath );

    boolean getPropertyBoolean( String absPath );

    long getPropertyLong( String absPath );

    double getPropertyDouble( String absPath );

    Date getPropertyDate( String absPath );

    DateTime getPropertyDateTime( String absPath );

    void setPropertyString( String absPath, String value );

    void setPropertyBoolean( String absPath, boolean value );

    void setPropertyLong( String absPath, long value );

    void setPropertyDouble( String absPath, double value );

    void setPropertyDate( String absPath, Date value );

    void setPropertyDateTime( String absPath, DateTime value );

    JcrQuery createQuery();
}
