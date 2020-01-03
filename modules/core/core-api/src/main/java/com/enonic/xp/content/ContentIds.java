package com.enonic.xp.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
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
        return doParseIds( Arrays.asList( paths ) );
    }

    private static ImmutableSet<ContentId> doParseIds( final Collection<String> list )
    {
        return list.stream().map( ContentId::from ).collect( ImmutableSet.toImmutableSet() );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( ContentId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private List<ContentId> contents = new ArrayList<>();

        public Builder add( final ContentId contentId )
        {
            this.contents.add( contentId );
            return this;
        }

        public Builder addAll( final ContentIds contentIds )
        {
            this.contents.addAll( contentIds.getSet() );
            return this;
        }


        public ContentIds build()
        {
            return ContentIds.from( contents );
        }
    }
}
