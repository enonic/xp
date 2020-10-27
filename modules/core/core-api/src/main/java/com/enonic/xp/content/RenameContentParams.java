package com.enonic.xp.content;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RenameContentParams
{
    private final ContentId contentId;

    private final ContentName newName;

    private final boolean stopInherit;

    public RenameContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.newName = builder.newName;
        this.stopInherit = builder.stopInherit;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ContentName getNewName()
    {
        return newName;
    }

    public boolean stopInherit()
    {
        return stopInherit;
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
        return Objects.equals( this.contentId, that.contentId ) && Objects.equals( this.newName, that.newName ) &&
            Objects.equals( this.stopInherit, that.stopInherit );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.contentId, this.newName, this.stopInherit );
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

        private boolean stopInherit = true;

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

        public Builder stopInherit( final boolean stopInherit )
        {
            this.stopInherit = stopInherit;
            return this;
        }

        public RenameContentParams build()
        {
            return new RenameContentParams( this );
        }
    }
}
