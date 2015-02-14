package com.enonic.xp.core.content;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.core.support.AbstractImmutableEntitySet;

public final class ContentIds
    extends AbstractImmutableEntitySet<ContentId>
    implements Iterable<ContentId>
{
    private ContentIds( final ImmutableSet<ContentId> set )
    {
        super( set );
    }

    public static ContentIds empty()
    {
        final ImmutableSet<ContentId> set = ImmutableSet.of();
        return new ContentIds( set );
    }

    public static ContentIds from( final ContentId... ids )
    {
        return new ContentIds( ImmutableSet.copyOf( ids ) );
    }

    public static ContentIds from( final String... ids )
    {
        return new ContentIds( parseIds( ids ) );
    }

    public static ContentIds from( final Collection<String> ids )
    {
        return new ContentIds( doParseIds( ids ) );
    }

    public static ContentIds from( final Iterable<ContentId> ids )
    {
        return new ContentIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<ContentId> parseIds( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        return doParseIds( list );
    }

    private static ImmutableSet<ContentId> doParseIds( final Collection<String> list )
    {
        final Collection<ContentId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, ContentId>
    {
        @Override
        public ContentId apply( final String value )
        {
            return ContentId.from( value );
        }
    }
}
