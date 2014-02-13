package com.enonic.wem.api.content.page;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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

    public PageTemplates filter(final PageTemplateSpec spec) {
        return PageTemplates.from( Collections2.filter( templatesByName.values(), new Predicate<PageTemplate>()
        {
            @Override
            public boolean apply( @Nullable final PageTemplate pageTemplate )
            {
                return spec.isSatisfiedBy( pageTemplate );
            }
        } ) );
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

    public PageTemplates sort()
    {
        final Comparator<PageTemplate> comparator = new Comparator<PageTemplate>()
        {
            @Override
            public int compare( final PageTemplate input1, final PageTemplate input2 )
            {
                String displayName1 = input1.getDisplayName();
                String displayName2 = input2.getDisplayName();

                return displayName1 == null ? -1 : displayName2 == null ? 1 : displayName1.compareTo( displayName2 );
            }
        };
        return sort( comparator );
    }

    public PageTemplates sort( Comparator<PageTemplate> comparator )
    {
        final List<PageTemplate> unordered = Lists.newArrayList( this.list );
        Collections.sort( unordered, comparator );

        return new PageTemplates( ImmutableList.copyOf( unordered ) );
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

        public Builder addAll( Iterable<PageTemplate> templates )
        {
            for( final PageTemplate template : templates )
            {
                this.list.add( template );
            }
            return this;
        }

        public PageTemplates build()
        {
            return new PageTemplates( this.list.build() );
        }
    }

}
