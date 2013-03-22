package com.enonic.wem.core.jcr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.value.StringValue;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Lists;

public final class JcrHelper
{

    private static DateTimeFormatter isoDateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

    private JcrHelper()
    {
    }

    public static Node getOrAddNode( final Node parent, final String relPath )
        throws RepositoryException
    {
        return JcrUtils.getOrAddNode( parent, relPath );
    }

    public static Node getOrAddNode( final Node parent, final String relPath, final String type )
        throws RepositoryException
    {
        return JcrUtils.getOrAddNode( parent, relPath, type );
    }

    public static Node getNodeOrNull( final Node parent, final String relPath )
        throws RepositoryException
    {
        if ( parent.hasNode( relPath ) )
        {
            return parent.getNode( relPath );
        }
        else
        {
            return null;
        }
    }

    public static Node getNodeById( final Session session, final String id )
        throws RepositoryException
    {
        try
        {
            return session.getNodeByIdentifier( id );
        }
        catch ( ItemNotFoundException e )
        {
            return null;
        }
        catch ( java.lang.IllegalArgumentException e )
        {
            // invalid id format for Oak
            return null;
        }
    }

    public static void removeNodes( final NodeIterator nodes )
        throws RepositoryException
    {
        while ( nodes.hasNext() )
        {
            nodes.nextNode().remove();
        }
    }

    public static void setPropertyDateTime( final Node node, final String propertyName, final DateTime value )
        throws RepositoryException
    {
        if ( value == null )
        {
            node.setProperty( propertyName, (String) null );
        }
        else
        {
            final String dateTimeValue = isoDateTimeFormatter.print( value );
            node.setProperty( propertyName, dateTimeValue );
        }
    }

    public static void setPropertyBinary( final Node node, final String propertyName, final byte[] value )
        throws RepositoryException
    {
        final Binary binaryValue = node.getSession().getValueFactory().createBinary( new ByteArrayInputStream( value ) );
        node.setProperty( propertyName, binaryValue );
    }

    public static void setPropertyBinary( final Node node, final String propertyName, final InputStream inputStream )
        throws RepositoryException
    {
        final Binary binaryValue = node.getSession().getValueFactory().createBinary( inputStream );
        node.setProperty( propertyName, binaryValue );
    }

    public static void setPropertyReference( final Node node, final String propertyName, Node... referencedNodes )
        throws RepositoryException
    {
        final Value[] values = new Value[referencedNodes.length];
        for ( int i = 0; i < referencedNodes.length; i++ )
        {
            //values[i] = new ReferenceValue( referencedNodes[i] ); // TODO not supported yet in OAK
            values[i] = new StringValue( referencedNodes[i].getPath() );
        }
        node.setProperty( propertyName, values );
    }

    public static Node[] getPropertyReferences( final Node node, final String propertyName )
        throws RepositoryException
    {
        final Property property = node.getProperty( propertyName );
        if ( property == null )
        {
            return new Node[0];
        }

        final Session session = node.getSession();
        final List<Node> nodeList = Lists.newArrayList();
        for ( Value value : property.getValues() )
        {
            final String path = value.toString();
            nodeList.add( session.getNode( path ) );
        }
        return nodeList.toArray( new Node[nodeList.size()] );
    }

    public static String getPropertyString( final Node node, final String propertyName )
        throws RepositoryException
    {
        return getPropertyString( node, propertyName, null );
    }

    public static String getPropertyString( final Node node, final String propertyName, final String defaultValue )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        return property == null ? defaultValue : property.getString();
    }

    public static Boolean getPropertyBoolean( final Node node, final String propertyName, final Boolean defaultValue )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        return property == null ? defaultValue : Boolean.valueOf( property.getBoolean() );
    }

    public static Boolean getPropertyBoolean( final Node node, final String propertyName )
        throws RepositoryException
    {
        return getPropertyBoolean( node, propertyName, null );
    }

    public static Long getPropertyLong( final Node node, final String propertyName, final Long defaultValue )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        return property == null ? defaultValue : property.getLong();
    }

    public static Long getPropertyLong( final Node node, final String propertyName )
        throws RepositoryException
    {
        return getPropertyLong( node, propertyName, null );
    }

    public static DateTime getPropertyDateTime( final Node node, final String propertyName )
        throws RepositoryException
    {
        final String value = getPropertyString( node, propertyName );
        return value == null ? null : isoDateTimeFormatter.parseDateTime( value );
    }

    public static byte[] getPropertyBinary( final Node node, final String propertyName )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        if ( property == null )
        {
            return null;
        }
        else
        {
            final Binary binaryValue = property.getValue().getBinary();
            try
            {
                return IOUtils.toByteArray( binaryValue.getStream() );
            }
            catch ( IOException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    public static InputStream getPropertyBinaryAsInputStream( final Node node, final String propertyName )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        if ( property == null )
        {
            return null;
        }
        else
        {
            final Binary binaryValue = property.getValue().getBinary();
            return binaryValue.getStream();
        }
    }

    private static Property getInternalProperty( final Node node, final String propertyName )
        throws RepositoryException
    {
        try
        {
            return node.getProperty( propertyName );
        }
        catch ( PathNotFoundException e )
        {
            return null;
        }

    }
}