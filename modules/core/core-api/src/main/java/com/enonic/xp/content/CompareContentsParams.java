package com.enonic.xp.content;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class CompareContentsParams
{
    private final ContentIds contentIds;

    private final Branch target;

    public CompareContentsParams( final ContentIds contentIds, final Branch target )
    {
        this.contentIds = contentIds;
        this.target = target;
    }

    public ContentIds getContentIds()
    {
        return contentIds;
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
        final CompareContentsParams that = (CompareContentsParams) o;
        return Objects.equals( contentIds, that.contentIds ) && Objects.equals( target, that.target );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentIds, target );
    }
}
