package com.enonic.xp.page;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.Contents;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class PageTemplates
    extends AbstractImmutableEntityList<PageTemplate>
{
    private final ImmutableMap<ContentName, PageTemplate> templatesByName;

    private PageTemplates( final ImmutableList<PageTemplate> list )
    {
        super( list );
        this.templatesByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public PageTemplate getTemplate( final ContentName name )
    {
        return this.templatesByName.get( name );
    }

    public PageTemplate getTemplate( final PageTemplateKey key )
    {
        for ( PageTemplate pageTemplate : this.templatesByName.values() )
        {
            if ( pageTemplate.getKey().equals( key ) )
            {
                return pageTemplate;
            }
        }
        return null;
    }

    public PageTemplates filter( final PageTemplateSpec spec )
    {
        return PageTemplates.from( Collections2.filter( templatesByName.values(), new Predicate<PageTemplate>()
        {
            @Override
            public boolean apply( final PageTemplate pageTemplate )
            {
                return spec.isSatisfiedBy( pageTemplate );
            }
        } ) );
    }

    public Contents toContents()
    {
        final Contents.Builder builder = Contents.builder();
        for ( final PageTemplate pageTemplate : this )
        {
            builder.add( pageTemplate );
        }
        return builder.build();
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
        implements Function<PageTemplate, ContentName>
    {
        @Override
        public ContentName apply( final PageTemplate value )
        {
            return value.getName();
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
            for ( final PageTemplate template : templates )
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
