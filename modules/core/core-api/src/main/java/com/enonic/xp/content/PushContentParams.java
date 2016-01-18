package com.enonic.xp.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class PushContentParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private final boolean includeChildren;

    private final boolean resolveDependencies;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        includeChildren = builder.includeChildren;
        resolveDependencies = builder.resolveDependencies;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public Branch getTarget()
    {
        return target;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public boolean isResolveDependencies()
    {
        return resolveDependencies;
    }

    @Override
    public boolean equals( final Object obj )
    {
        PushContentParams other;
        if ( obj instanceof PushContentParams )
        {
            other = (PushContentParams) obj;
        }
        else
        {
            return false;
        }

        if ( this.isIncludeChildren() != other.isIncludeChildren() )
        {
            return false;
        }

        if ( this.isResolveDependencies() != other.isResolveDependencies() )
        {
            return false;
        }

        if ( !this.getTarget().equals( other.getTarget() ) )
        {
            return false;
        }

        if ( this.getContentIds().equals( other.getContentIds() ) )
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private Branch target;

        private boolean includeChildren = true;

        private boolean resolveDependencies = true;

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }

        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public Builder resolveDependencies( final boolean resolveDependencies )
        {
            this.resolveDependencies = resolveDependencies;
            return this;
        }

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}