package com.enonic.wem.api.content.versioning;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ContentVersionHistory
    extends AbstractImmutableEntityList<ContentVersion>
{
    private ContentVersionHistory( final ImmutableList<ContentVersion> list )
    {
        super( list );
    }

    public static ContentVersionHistory empty()
    {
        final ImmutableList<ContentVersion> list = ImmutableList.of();
        return new ContentVersionHistory( list );
    }

    public static ContentVersionHistory from( final ContentVersion... contentVersions )
    {
        return new ContentVersionHistory( ImmutableList.copyOf( contentVersions ) );
    }

    public static ContentVersionHistory from( final Iterable<? extends ContentVersion> contentVersions )
    {
        return new ContentVersionHistory( ImmutableList.copyOf( contentVersions ) );
    }

    public static ContentVersionHistory from( final Collection<? extends ContentVersion> contentVersions )
    {
        return new ContentVersionHistory( ImmutableList.copyOf( contentVersions ) );
    }
}
