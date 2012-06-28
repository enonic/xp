package com.enonic.wem.core.jcr.accounts;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

abstract class JcrAccountBase
        implements JcrAccount
{
    private static final char USER_STORE_SEPARATOR = '\\';

    private final JcrAccountType type;

    private String id;

    private String name;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private DateTime lastModified;

    private boolean builtIn;


    protected JcrAccountBase( JcrAccountType type )
    {
        this.type = type;
    }

    public boolean isUser()
    {
        return this.type == JcrAccountType.USER;
    }

    public boolean isGroup()
    {
        return this.type == JcrAccountType.GROUP;
    }

    public boolean isRole()
    {
        return this.type == JcrAccountType.ROLE;
    }

    public JcrAccountType getAccountType()
    {
        return this.type;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
        qualifiedName = null;
    }

    public String getQualifiedName()
    {
        if ( qualifiedName == null )
        {
            if ( name != null )
            {
                qualifiedName = userStore != null ? userStore + USER_STORE_SEPARATOR + name : name;
            }
        }
        return qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
        qualifiedName = null;
    }

    public DateTime getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( DateTime lastModified )
    {
        this.lastModified = lastModified;
    }

    public boolean isBuiltIn()
    {
        return builtIn;
    }

    public void setBuiltIn( boolean builtIn )
    {
        this.builtIn = builtIn;
    }

    public JcrAccountType getType()
    {
        return this.type;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof JcrAccount ) )
        {
            return false;
        }

        final JcrAccount that = (JcrAccount) o;

        if ( getId() != null ? !getId().equals( that.getId() ) : that.getId() != null )
        {
            return false;
        }
        if ( getType() != that.getType() )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append( id ).append( type ).toHashCode();
    }
}
