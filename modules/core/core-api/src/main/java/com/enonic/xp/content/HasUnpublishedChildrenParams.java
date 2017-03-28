package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.security.PrincipalKey;

@Beta
public final class HasUnpublishedChildrenParams
{
    private ContentId contentId;

    private final Branch target;

    public HasUnpublishedChildrenParams( final ContentId contentId, final Branch target )
    {
        this.contentId = contentId;
        this.target = target;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branch getTarget() {
        return target;
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
        if ( !( o instanceof HasUnpublishedChildrenParams ) )
        {
            return false;
        }

        final HasUnpublishedChildrenParams that = (HasUnpublishedChildrenParams) o;

        if ( !contentId.equals( that.contentId ) )
        {
            return false;
        }

        if( !target.equals( that.target )) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentId, target );
    }
}
