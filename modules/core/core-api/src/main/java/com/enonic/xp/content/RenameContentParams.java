package com.enonic.xp.content;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RenameContentParams
{
    private ContentId contentId;

    private ContentName newName;

    public RenameContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.newName = builder.newName;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentName getNewName()
    {
        return newName;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof RenameContentParams ) )
        {
            return false;
        }

        final RenameContentParams that = (RenameContentParams) o;
        return Objects.equals( this.contentId, that.contentId ) && Objects.equals( this.newName, that.newName );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.contentId, this.newName );
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
        Preconditions.checkNotNull( this.newName, "name cannot be null" );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ContentId contentId;

        private ContentName newName;

        public Builder contentId( final ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder newName( final ContentName newName )
        {
            this.newName = newName;
            return this;
        }

        public RenameContentParams build()
        {
            return new RenameContentParams( this );
        }
    }
}
