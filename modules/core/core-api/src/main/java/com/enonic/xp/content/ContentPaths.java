package com.enonic.xp.content;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class ContentPaths
    extends AbstractImmutableEntitySet<ContentPath>
    implements Iterable<ContentPath>
{
    private ContentPaths( final ImmutableSet<ContentPath> set )
    {
        super( set );
    }

    public static ContentPaths empty()
    {
        return new ContentPaths( ImmutableSet.of() );
    }

    public static ContentPaths from( final String... paths )
    {
        return new ContentPaths( parsePaths( Arrays.asList( paths ) ) );
    }

    public static ContentPaths from( final Collection<String> contentPaths )
    {
        return new ContentPaths( parsePaths( contentPaths ) );
    }

    public static ContentPaths from( final ContentPath... paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static ContentPaths from( final Iterable<ContentPath> paths )
    {
        return new ContentPaths( ImmutableSet.copyOf( paths ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private static ImmutableSet<ContentPath> parsePaths( final Collection<String> paths )
    {
        return paths.stream().map( ContentPath::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public static class Builder
    {
        private ImmutableList.Builder<ContentPath> paths = ImmutableList.builder();

        public Builder add( final ContentPath contentPath )
        {
            this.paths.add( contentPath );
            return this;
        }

        public Builder addAll( final ContentPaths contentPaths )
        {
            this.paths.addAll( contentPaths.getSet() );
            return this;
        }

        public ContentPaths build()
        {
            return ContentPaths.from( paths.build() );
        }
    }
}
