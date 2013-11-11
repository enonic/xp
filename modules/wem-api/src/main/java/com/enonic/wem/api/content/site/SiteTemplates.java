package com.enonic.wem.api.content.site;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class SiteTemplates
    extends AbstractImmutableEntityList<SiteTemplate>
{
    private final ImmutableMap<SiteTemplateName, SiteTemplate> map;

    private SiteTemplates( final ImmutableList<SiteTemplate> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<SiteTemplateName> getNames()
    {
        return map.keySet();
    }

    public SiteTemplate getSiteTemplate( final SiteTemplateName name )
    {
        return map.get( name );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static SiteTemplates empty()
    {
        final ImmutableList<SiteTemplate> list = ImmutableList.of();
        return new SiteTemplates( list );
    }

    public static SiteTemplates from( final SiteTemplate... siteTemplates )
    {
        return new SiteTemplates( ImmutableList.copyOf( siteTemplates ) );
    }

    public static SiteTemplates from( final Iterable<SiteTemplate> siteTemplates )
    {
        return new SiteTemplates( ImmutableList.copyOf( siteTemplates ) );
    }

    public static SiteTemplates from( final Collection<SiteTemplate> siteTemplates )
    {
        return new SiteTemplates( ImmutableList.copyOf( siteTemplates ) );
    }

    private final static class ToNameFunction
        implements Function<SiteTemplate, SiteTemplateName>
    {
        @Override
        public SiteTemplateName apply( final SiteTemplate value )
        {
            return value.getName();
        }
    }
}
