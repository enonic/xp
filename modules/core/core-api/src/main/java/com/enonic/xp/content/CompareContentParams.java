package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;

@Beta
public class CompareContentParams
{
    private final ContentId contentId;

    private final Branch target;

    public CompareContentParams( final ContentId contentId, final Branch target )
    {
        this.contentId = contentId;
        this.target = target;
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public Branch getTarget()
    {
        return target;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final CompareContentParams that = (CompareContentParams) o;
        return Objects.equals( contentId, that.contentId ) && Objects.equals( target, that.target );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentId, target );
    }
}
