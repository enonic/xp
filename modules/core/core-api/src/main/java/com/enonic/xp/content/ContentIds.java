package com.enonic.xp.content;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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

    public Set<String> asStrings()
    {
        return this.set.stream().map( ContentId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
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

    public static class Builder
    {
        private List<ContentId> contents = Lists.newArrayList();

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

        public Builder addAll( final Collection<ContentId> contentIds )
        {
            this.contents.addAll( contentIds );
            return this;
        }


        public ContentIds build()
        {
            return ContentIds.from( contents );
        }
    }
}
