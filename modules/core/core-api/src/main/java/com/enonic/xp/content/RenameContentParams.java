package com.enonic.xp.content;


import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class RenameContentParams
{
    private final ContentId contentId;

    private final ContentName newName;

    private final boolean stopInherit;

    private RenameContentParams( final Builder builder )
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ContentName newName;

        private boolean stopInherit = true;

        private Builder()
        {
        }

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
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            Preconditions.checkNotNull( this.newName, "name cannot be null" );
            return new RenameContentParams( this );
        }
    }
}
