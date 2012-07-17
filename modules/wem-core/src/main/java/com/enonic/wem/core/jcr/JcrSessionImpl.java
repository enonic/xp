package com.enonic.wem.core.jcr;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

class JcrSessionImpl
    implements JcrSession
{
    // UUID pattern: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
    private final static Pattern NODE_ID_PATTERN = Pattern.compile( "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}" );

    private final Session session;

    private final JcrRepository repository;


    JcrSessionImpl( Session session, JcrRepository repository )
    {
        this.session = session;
        this.repository = repository;
    }

    public Session getRealSession()
    {
        return session;
    }

    public JcrRepository getRepository()
    {
        return repository;
    }

    public void logout()
    {
        if ( session.isLive() )
        {
            session.logout();
        }
    }

    public void save()
    {
        try
        {
            session.save();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrNode getNodeByIdentifier( final String id )
    {
        // check id format to avoid generic RepositoryException thrown
        if ( !isValidUUID( id ) )
        {
            return null;
        }
        try
        {
            return new JcrNodeImpl( session.getNodeByIdentifier( id ) );
        }
        catch ( ItemNotFoundException nfe )
        {
            return null;
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public JcrNode getRootNode()
    {
        try
        {
            return new JcrNodeImpl( session.getRootNode() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public JcrNode getNode( String absPath )
    {
        try
        {
            return new JcrNodeImpl( session.getNode( absPath ) );
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

    public JcrNode getOrCreateNode( String absPath )
    {
        try
        {
            return new JcrNodeImpl( session.getNode( absPath ) );
        }
        catch ( PathNotFoundException e )
        {
            try
            {
                return new JcrNodeImpl( session.getRootNode().addNode( StringUtils.removeStart( absPath, "/" ) ) );
            }
            catch ( RepositoryException re )
            {
                throw new RepositoryRuntimeException( re );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public JcrNode getNode( Node node )
    {
        return new JcrNodeImpl( node );
    }

    public boolean nodeExists( String absPath )
    {
        try
        {
            return session.nodeExists( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void removeItem( String absPath )
    {
        try
        {
            session.removeItem( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public boolean propertyExists( String absPath )
    {
        try
        {
            return session.propertyExists( absPath );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Property getProperty( String absPath )
    {
        try
        {
            return session.getProperty( absPath );
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

    public String getPropertyString( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getString();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public boolean getPropertyBoolean( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getBoolean();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public long getPropertyLong( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getLong();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public double getPropertyDouble( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : property.getDouble();
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public Date getPropertyDate( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : calendarToDate( property.getDate() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public DateTime getPropertyDateTime( String absPath )
    {
        Property property = getProperty( absPath );
        try
        {
            return property == null ? null : new DateTime( property.getDate() );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyString( String absPath, String value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyBoolean( String absPath, boolean value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyLong( String absPath, long value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyDouble( String absPath, double value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyDate( String absPath, Date value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( dateToCalendar( value ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    public void setPropertyDateTime( String absPath, DateTime value )
    {
        try
        {
            Property property = session.getProperty( absPath );
            property.setValue( value.toCalendar( Locale.getDefault() ) );
        }
        catch ( RepositoryException e )
        {
            throw new RepositoryRuntimeException( e );
        }
    }

    @Override
    public JcrQuery createQuery()
    {
        return new JcrQuery( this );
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

    private boolean isValidUUID( final String id )
    {
        return NODE_ID_PATTERN.matcher( id ).matches();
    }
}
