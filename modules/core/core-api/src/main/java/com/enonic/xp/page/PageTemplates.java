package com.enonic.xp.page;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class PageTemplates
    extends AbstractImmutableEntityList<PageTemplate>
{
    private static final PageTemplates EMPTY = new PageTemplates( ImmutableList.of() );

    private PageTemplates( final ImmutableList<PageTemplate> list )
    {
        super( list );
    }

    public PageTemplate getTemplate( final ContentName name )
    {
        return this.list.stream().filter( pT -> name.equals( pT.getName() ) ).findFirst().orElse( null );
    }

    public PageTemplate getTemplate( final PageTemplateKey key )
    {
        return this.list.stream().filter( pT -> key.equals( pT.getKey() ) ).findFirst().orElse( null );
    }

    public PageTemplates filter( final Predicate<PageTemplate> predicate )
    {
        return PageTemplates.from( this.stream().filter( predicate ).toArray( PageTemplate[]::new ) );
    }

    public static PageTemplates empty()
    {
        return EMPTY;
    }

    public static PageTemplates from( final PageTemplate... templates )
    {
        return fromInternal( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Iterable<? extends PageTemplate> templates )
    {
        return fromInternal( ImmutableList.copyOf( templates ) );
    }

    public static PageTemplates from( final Collection<? extends PageTemplate> templates )
    {
        return fromInternal( ImmutableList.copyOf( templates ) );
    }

    public static Collector<PageTemplate, ?, PageTemplates> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), PageTemplates::fromInternal );
    }

    private static PageTemplates fromInternal( final ImmutableList<PageTemplate> list )
    {
        return list.isEmpty() ? EMPTY : new PageTemplates( list );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<PageTemplate> list = new ImmutableList.Builder<>();

        public Builder add( PageTemplate template )
        {
            this.list.add( template );
            return this;
        }

        public Builder addAll( Iterable<? extends PageTemplate> templates )
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
