package com.enonic.xp.page;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.Contents;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PageTemplates
    extends AbstractImmutableEntityList<PageTemplate>
{
    private final ImmutableMap<ContentName, PageTemplate> templatesByName;

    private PageTemplates( final ImmutableList<PageTemplate> list )
    {
        super( list );
        this.templatesByName = list.stream().collect( ImmutableMap.toImmutableMap( PageTemplate::getName, Function.identity() ) );
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

    public PageTemplates filter( final Predicate<PageTemplate> predicate )
    {
        return PageTemplates.from( this.stream().filter( predicate ).toArray( PageTemplate[]::new ) );
    }

    public Contents toContents()
    {
        final Contents.Builder builder = Contents.create();
        for ( final PageTemplate pageTemplate : this )
        {
            builder.add( pageTemplate );
        }
        return builder.build();
    }

    public static PageTemplates empty()
    {
        return new PageTemplates( ImmutableList.of() );
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

    public static Builder create()
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
