package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.node.DuplicateValueResolver;
import com.enonic.xp.security.PrincipalKey;

@Beta
public final class DuplicateContentParams
{
    private ContentId contentId;

    private PrincipalKey creator;

    private DuplicateContentProcessor processor;

    private DuplicateValueResolver valueResolver;

    private DuplicateContentListener duplicateContentListener;

    private Boolean includeChildren;

    private ContentPath parent;

    private ContentPath dependenciesToDuplicatePath;

    public DuplicateContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.creator = builder.creator;
        this.processor = builder.processor;
        this.valueResolver = builder.valueResolver;
        this.duplicateContentListener = builder.duplicateContentListener;
        this.includeChildren = builder.includeChildren;
        this.parent = builder.parent;
        this.dependenciesToDuplicatePath = builder.dependenciesToDuplicatePath;
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

    public DuplicateContentProcessor getProcessor()
    {
        return processor;
    }

    public DuplicateValueResolver getValueResolver()
    {
        return valueResolver;
    }

    public ContentPath getParent()
    {
        return parent;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public ContentPath getDependenciesToDuplicatePath()
    {
        return dependenciesToDuplicatePath;
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

        if ( !parent.equals( that.parent ) )
        {
            return false;
        }

        if ( !dependenciesToDuplicatePath.equals( that.dependenciesToDuplicatePath ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( contentId, creator, includeChildren, parent, dependenciesToDuplicatePath );
    }

    public static final class Builder
    {

        private ContentId contentId;

        private PrincipalKey creator;

        private DuplicateContentListener duplicateContentListener;

        private DuplicateContentProcessor processor;

        private DuplicateValueResolver valueResolver;

        private Boolean includeChildren = true;

        private ContentPath parent;

        private ContentPath dependenciesToDuplicatePath;

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

        public Builder processor( final DuplicateContentProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        public Builder valueResolver( final DuplicateValueResolver valueResolver )
        {
            this.valueResolver = valueResolver;
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

        public Builder parent( final ContentPath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder dependenciesToDuplicatePath( final ContentPath dependenciesToDuplicatePath )
        {
            this.dependenciesToDuplicatePath = dependenciesToDuplicatePath;
            return this;
        }

        public DuplicateContentParams build()
        {
            return new DuplicateContentParams( this );
        }
    }
}
