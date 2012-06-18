package com.enonic.wem.core.jcr;

import java.util.Calendar;
import java.util.Date;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.value.BinaryValue;

class JcrNodeImpl
        implements JcrNode
{
    private Node node;

    private JcrNode parent;

    JcrNodeImpl( final Node node )
    {
        this.node = node;
        this.parent = null;
    }

    @Override
    public boolean hasProperty( String relPath )
    {
        try
        {
            return node.hasProperty( relPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public Property getProperty( String relPath )
    {
        try
        {
            return node.getProperty( relPath );
        }
        catch ( PathNotFoundException e )
        {
            return null;
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public String getPropertyString( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : property.getString();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public Boolean getPropertyBoolean( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : property.getBoolean();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public long getPropertyLong( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : property.getLong();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public double getPropertyDouble( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : property.getDouble();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public Date getPropertyDate( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : calendarToDate( property.getDate() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public Calendar getPropertyCalendar( String relPath )
    {
        Property property = getProperty( relPath );
        try
        {
            return property == null ? null : property.getDate();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyString( String relPath, String value )
    {
        try
        {
            node.setProperty( relPath, value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyBoolean( String relPath, boolean value )
    {
        try
        {
            node.setProperty( relPath, value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyLong( String relPath, long value )
    {
        try
        {
            node.setProperty( relPath, value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyDouble( String relPath, double value )
    {
        try
        {
            node.setProperty( relPath, value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyDate( String relPath, Date value )
    {
        try
        {
            node.setProperty( relPath, dateToCalendar( value ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyCalendar( String relPath, Calendar value )
    {
        try
        {
            node.setProperty( relPath, value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setPropertyBinary( String relPath, byte[] value )
    {
        try
        {
            node.setProperty( relPath, new BinaryValue( value ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public String getName()
    {
        try
        {
            return node.getName();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode getParent()
    {
        if ( parent == null )
        {
            try
            {
                parent = new JcrNodeImpl( node.getParent() );
            }
            catch ( RepositoryException e )
            {
                throw new RepositoryRuntimeException( e );
            }
        }
        return parent;
    }

    @Override
    public JcrNode getNode( String relPath )
    {
        try
        {
            return new JcrNodeImpl( node.getNode( relPath ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public boolean hasNode( String relPath )
    {
        try
        {
            return node.hasNode( relPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode addNode( String relPath )
    {
        try
        {
            return new JcrNodeImpl( node.addNode( relPath ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode addNode( String relPath, String primaryNodeTypeName )
    {
        try
        {
            return new JcrNodeImpl( node.addNode( relPath, primaryNodeTypeName ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    private Calendar dateToCalendar( Date date )
    {
        if ( date == null )
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    private Date calendarToDate( Calendar calendar )
    {
        return calendar == null ? null : calendar.getTime();
    }
}
