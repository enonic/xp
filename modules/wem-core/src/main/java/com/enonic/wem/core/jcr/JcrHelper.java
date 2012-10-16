package com.enonic.wem.core.jcr;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.value.BinaryValue;
import org.apache.jackrabbit.value.StringValue;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public abstract class JcrHelper
{
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
            node.setProperty( propertyName, (Calendar) null );
        }
        else
        {
            node.setProperty( propertyName, value.toGregorianCalendar() );
        }
    }

    public static void setPropertyDateMidnight( final Node node, final String propertyName, final DateMidnight value )
        throws RepositoryException
    {
        if ( value == null )
        {
            node.setProperty( propertyName, (Calendar) null );
        }
        else
        {
            node.setProperty( propertyName, value.toGregorianCalendar() );
        }
    }

    public static void setPropertyBinary( final Node node, final String propertyName, final byte[] value )
        throws RepositoryException
    {
        node.setProperty( propertyName, new BinaryValue( value ) );
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
        Property property = getInternalProperty( node, propertyName );
        return property == null ? null : property.getString();
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

    public static DateTime getPropertyDateTime( final Node node, final String propertyName )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        return property == null ? null : new DateTime( property.getDate() );
    }

    public static DateMidnight getPropertyDateMidnight( final Node node, final String propertyName )
        throws RepositoryException
    {
        Property property = getInternalProperty( node, propertyName );
        return property == null ? null : new DateMidnight( property.getDate() );
    }

    public static byte[] getPropertyBinary( final Node node, final String propertyName )
        throws RepositoryException, IOException
    {
        Property property = getInternalProperty( node, propertyName );
        if ( property == null )
        {
            return null;
        }
        else
        {
            final Binary binaryValue = property.getValue().getBinary();
            return IOUtils.toByteArray( binaryValue.getStream() );
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