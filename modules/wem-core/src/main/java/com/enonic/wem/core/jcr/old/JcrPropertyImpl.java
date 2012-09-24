package com.enonic.wem.core.jcr.old;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.Binary;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.value.BinaryValue;
import org.joda.time.DateTime;

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
    public void setValue( Date value )
    {
        try
        {
            property.setValue( dateToCalendar( value ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public void setValue( final DateTime value )
    {
        try
        {
            if ( value == null )
            {
                property.setValue( (Calendar) null );
            }
            else
            {
                property.setValue( value.toGregorianCalendar() );
            }
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
    public void setValue( final byte[] value )
    {
        try
        {
            property.setValue( new BinaryValue( value ) );
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
    public Date getDate()
    {
        try
        {
            return calendarToDate( property.getDate() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public DateTime getDateTime()
    {
        try
        {
            return property == null ? null : new DateTime( property.getDate() );
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

    @Override
    public byte[] getBinary()
    {
        try
        {
            final Binary binaryValue = property.getValue().getBinary();
            return IOUtils.toByteArray( binaryValue.getStream() );
        }
        catch ( IOException e )
        {
            throw new RepositoryRuntimeException( e );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode getParent()
    {
        try
        {
            return new JcrNodeImpl( property.getParent() );
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
