package com.enonic.wem.api.space;

import java.util.Arrays;

import org.joda.time.DateTime;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.ContentId;

public final class Space
{
    private final String displayName;

    private final SpaceName name;

    private final DateTime createdTime;

    private final DateTime modifiedTime;

    private final ContentId rootContent;

    private final byte[] icon;

    private Space( final Builder builder )
    {
        this.displayName = builder.displayName;
        this.name = builder.name;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.rootContent = builder.rootContent;
        this.icon = builder.icon;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public DateTime getModifiedTime()
    {
        return modifiedTime;
    }

    public SpaceName getName()
    {
        return name;
    }

    public ContentId getRootContent()
    {
        return rootContent;
    }

    public byte[] getIcon()
    {
        return icon;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof Space ) )
        {
            return false;
        }
        final Space that = (Space) o;
        return Objects.equal( this.name, that.name ) &&
            Objects.equal( this.displayName, that.displayName ) &&
            ( this.createdTime == that.createdTime || ( this.createdTime != null && this.createdTime.isEqual( that.createdTime ) ) ) &&
            ( this.modifiedTime == that.modifiedTime || ( this.modifiedTime != null && this.modifiedTime.isEqual( that.modifiedTime ) ) ) &&
            Objects.equal( this.rootContent, that.rootContent ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, createdTime, modifiedTime, rootContent, icon );
    }

    @Override
    public String toString()
    {
        final Objects.ToStringHelper s = Objects.toStringHelper( this );
        s.add( "name", name );
        s.add( "displayName", displayName );
        s.add( "createdTime", createdTime );
        s.add( "modifiedTime", modifiedTime );
        s.add( "rootContent", rootContent );
        s.add( "icon", icon );
        return s.toString();
    }

    public static Builder newSpace()
    {
        return new Builder();
    }

    public static Builder newSpace( final Space space )
    {
        return new Builder( space );
    }

    public static class Builder
    {
        private String displayName;

        private SpaceName name;

        private DateTime createdTime;

        private DateTime modifiedTime;

        private ContentId rootContent;

        private byte[] icon;

        private Builder()
        {
            this.displayName = null;
            this.name = null;
            this.createdTime = null;
            this.modifiedTime = null;
            this.rootContent = null;
            this.icon = null;
        }

        private Builder( final Space space )
        {
            this.displayName = space.displayName;
            this.name = space.name;
            this.createdTime = space.createdTime;
            this.modifiedTime = space.modifiedTime;
            this.rootContent = space.rootContent;
            this.icon = space.icon == null ? null : Arrays.copyOf( space.icon, space.icon.length );
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder name( final SpaceName name )
        {
            this.name = name;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = SpaceName.from( name );
            return this;
        }

        public Builder createdTime( final DateTime createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime( final DateTime modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder rootContent( final ContentId rootContentId )
        {
            this.rootContent = rootContentId;
            return this;
        }

        public Builder icon( final byte[] icon )
        {
            this.icon = icon;
            return this;
        }

        public Space build()
        {
            Preconditions.checkNotNull( name, "name is mandatory for a space" );
            return new Space( this );
        }
    }
}
