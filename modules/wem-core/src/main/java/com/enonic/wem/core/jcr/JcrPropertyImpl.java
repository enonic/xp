package com.enonic.wem.core.jcr;

import java.util.Calendar;

import javax.jcr.Property;
import javax.jcr.RepositoryException;

class JcrPropertyImpl
        implements JcrProperty
{
    private final Property property;

    JcrPropertyImpl( Property property )
    {
        this.property = property;
    }

    @Override
    public void setValue( String value )
    {
        try
        {
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( String[] values )
    {
        try
        {
            property.setValue( values );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( long value )
    {
        try
        {
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( double value )
    {
        try
        {
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( Calendar value )
    {
        try
        {
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( boolean value )
    {
        try
        {
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( JcrNode value )
    {
        try
        {
            property.setValue( ( (JcrNodeImpl) value ).getInternalNode() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public String getString()
    {
        try
        {
            return property.getString();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public long getLong()
    {
        try
        {
            return property.getLong();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public double getDouble()
    {
        try
        {
            return property.getDouble();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public Calendar getDate()
    {
        try
        {
            return property.getDate();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public boolean getBoolean()
    {
        try
        {
            return property.getBoolean();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode getNode()
    {
        try
        {
            return new JcrNodeImpl( property.getNode() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }
}
