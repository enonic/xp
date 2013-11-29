package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class PageTemplates
    extends AbstractImmutableEntityList<PageTemplate>
{
    private final ImmutableMap<PageTemplateName, PageTemplate> templatesByName;

    private final ImmutableMap<ResourcePath, PageTemplate> templatesByPath;

    private PageTemplates( final ImmutableList<PageTemplate> list )
    {
        super( list );
        this.templatesByName = Maps.uniqueIndex( list, new ToNameFunction() );
        this.templatesByPath = Maps.uniqueIndex( list, new ToPathFunction() );
    }

    public PageTemplate getTemplate( final PageTemplateName name )
    {
        return this.templatesByName.get( name );
    }

    public PageTemplate getTemplate( final ResourcePath path )
    {
        return this.templatesByPath.get( path );
    }

    public static PageTemplates empty()
    {
        final ImmutableList<PageTemplate> list = ImmutableList.of();
        return new PageTemplates( list );
    }

    public static PageTemplates from( final PageTemplate... templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Iterable<? extends PageTemplate> templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Collection<? extends PageTemplate> templates )
    {
        return new PageTemplates( ImmutableList.copyOf( templates ) );
    }

    private final static class ToNameFunction
        implements Function<PageTemplate, PageTemplateName>
    {
        @Override
        public PageTemplateName apply( final PageTemplate value )
        {
            return value.getName();
        }
    }

    private final static class ToPathFunction
        implements Function<PageTemplate, ResourcePath>
    {
        @Override
        public ResourcePath apply( final PageTemplate value )
        {
            return value.getPath();
        }
    }

    public static Builder newPageTemplates()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<PageTemplate> list = new ImmutableList.Builder<>();

        public Builder add( PageTemplate template )
        {
            this.list.add( template );
            return this;
        }

        public PageTemplates build()
        {
            return new PageTemplates( this.list.build() );
        }
    }

}
