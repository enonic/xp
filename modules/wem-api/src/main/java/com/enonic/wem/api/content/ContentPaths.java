package com.enonic.wem.api.content;

import java.util.Collection;
import java.util.HashSet;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.api.AbstractImmutableEntitySet;

public final class ContentPaths
    extends AbstractImmutableEntitySet<ContentPath>
    implements ContentSelectors<ContentPath>
{
    private ContentPaths( final ImmutableSet<ContentPath> set )
    {
        super( set );
    }

    public ContentPaths add( final String... paths )
    {
        return add( parsePaths( paths ) );
    }

    public ContentPaths add( final ContentPath... paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths add( final Iterable<ContentPath> paths )
    {
        return add( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths add( final ImmutableSet<ContentPath> paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.addAll( paths );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    public ContentPaths remove( final String... paths )
    {
        return remove( parsePaths( paths ) );
    }

    public ContentPaths remove( final ContentPath... paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    public ContentPaths remove( final Iterable<ContentPath> paths )
    {
        return remove( ImmutableSet.copyOf( paths ) );
    }

    private ContentPaths remove( final ImmutableSet<ContentPath> paths )
    {
        final HashSet<ContentPath> tmp = Sets.newHashSet();
        tmp.addAll( this.set );
        tmp.removeAll( paths );
        return new ContentPaths( ImmutableSet.copyOf( tmp ) );
    }

    public static ContentPaths empty()
    {
        final ImmutableSet<ContentPath> list = ImmutableSet.of();
        return new ContentPaths( list );
    }

    public static ContentPaths from( final String... paths )
    {
        return new ContentPaths( parsePaths( paths ) );
    }

    public static ContentPaths from( final ContentPath... paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static ContentPaths from( final Iterable<ContentPath> paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    private static ImmutableSet<ContentPath> parsePaths( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<ContentPath> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }


    private final static class ParseFunction
        implements Function<String, ContentPath>
    {
        @Override
        public ContentPath apply( final String value )
        {
            return ContentPath.from( value );
        }
    }
}
