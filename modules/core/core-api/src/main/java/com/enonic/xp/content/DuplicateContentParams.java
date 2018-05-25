package com.enonic.xp.content;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.security.PrincipalKey;

@Beta
public final class DuplicateContentParams
{
    private ContentId contentId;

    private PrincipalKey creator;

    private DuplicateContentListener duplicateContentListener;

    private Boolean includeChildren;

    public DuplicateContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.creator = builder.creator;
        this.duplicateContentListener = builder.duplicateContentListener;
        this.includeChildren = builder.includeChildren;
    }

    public static DuplicateContentParams.Builder create()
    {
        return new DuplicateContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public DuplicateContentParams creator( final PrincipalKey creator )
    {
        this.creator = creator;
        return this;
    }

    public DuplicateContentListener getDuplicateContentListener()
    {
        return duplicateContentListener;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public void validate()
    {
        Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof DuplicateContentParams ) )
        {
            return false;
        }

        final DuplicateContentParams that = (DuplicateContentParams) o;

        if ( !contentId.equals( that.contentId ) )
        {
            return false;
        }

        if ( !includeChildren.equals( that.includeChildren ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return contentId.hashCode();
    }

    public static final class Builder
    {

        private ContentId contentId;

        private PrincipalKey creator;

        private DuplicateContentListener duplicateContentListener;

        private Boolean includeChildren = true;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder creator( PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder duplicateContentListener( DuplicateContentListener duplicateContentListener )
        {
            this.duplicateContentListener = duplicateContentListener;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public DuplicateContentParams build()
        {
            return new DuplicateContentParams( this );
        }
    }
}
