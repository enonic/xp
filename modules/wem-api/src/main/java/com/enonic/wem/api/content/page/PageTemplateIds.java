package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public final class PageTemplateIds
    extends AbstractImmutableEntitySet<PageTemplateId>
{
    private PageTemplateIds( final ImmutableSet<PageTemplateId> set )
    {
        super( set );
    }

    public static PageTemplateIds empty()
    {
        final ImmutableSet<PageTemplateId> set = ImmutableSet.of();
        return new PageTemplateIds( set );
    }

    public static PageTemplateIds from( final PageTemplateId... ids )
    {
        return new PageTemplateIds( ImmutableSet.copyOf( ids ) );
    }

    public static PageTemplateIds from( final String... ids )
    {
        return new PageTemplateIds( parseIds( ids ) );
    }

    public static PageTemplateIds from( final Iterable<PageTemplateId> ids )
    {
        return new PageTemplateIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<PageTemplateId> parseIds( final String... paths )
    {
        final Collection<String> list = Lists.newArrayList( paths );
        final Collection<PageTemplateId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    private final static class ParseFunction
        implements Function<String, PageTemplateId>
    {
        @Override
        public PageTemplateId apply( final String value )
        {
            return new PageTemplateId( value );
        }
    }
}
