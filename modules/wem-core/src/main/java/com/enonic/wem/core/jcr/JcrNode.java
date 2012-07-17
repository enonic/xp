package com.enonic.wem.core.jcr;

import java.util.Date;

import org.joda.time.DateTime;

public interface JcrNode
{
    String getIdentifier();

    String getName();

    boolean isNodeType( String nodeTypeName );

    String getPath();

    JcrNode getParent();

    void addMixin(String mixinName);

    boolean hasProperty( String relPath );

    JcrProperty getProperty( String relPath );

    String getPropertyString( String relPath );

    Boolean getPropertyBoolean( String relPath );

    Boolean getPropertyBoolean( String relPath, boolean defaultValue );

    byte[] getPropertyBinary( String relPath );

    long getPropertyLong( String relPath );

    double getPropertyDouble( String relPath );

    Date getPropertyDate( String relPath );

    DateTime getPropertyDateTime( String relPath );

    JcrNode getPropertyReference( String relPath );

    void setPropertyString( String relPath, String value );

    void setPropertyBoolean( String relPath, boolean value );

    void setPropertyLong( String relPath, long value );

    void setPropertyDouble( String relPath, double value );

    void setPropertyDate( String relPath, Date value );

    void setPropertyDateTime( String relPath, DateTime value );

    void setPropertyBinary( String relPath, byte[] value );

    void setPropertyReference( String name, JcrNode value );

    JcrNode getNode( String relPath );

    boolean hasNode( String relPath );

    JcrNode addNode( String relPath );

    JcrNode addNode( String relPath, String primaryNodeTypeName );

    JcrNodeIterator getNodes( String namePattern );

    JcrPropertyIterator getReferences( String name );

    JcrPropertyIterator getReferences();

    void remove();
}
