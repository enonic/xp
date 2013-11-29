package com.enonic.wem.api.content.page.layout;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class LayoutTemplates
    extends AbstractImmutableEntityList<LayoutTemplate>
{
    private final ImmutableMap<LayoutTemplateName, LayoutTemplate> templatesByName;

    private LayoutTemplates( final ImmutableList<LayoutTemplate> list )
    {
        super( list );
        this.templatesByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public LayoutTemplate getTemplate( final LayoutTemplateName name )
    {
        return this.templatesByName.get( name );
    }

    public static LayoutTemplates empty()
    {
        final ImmutableList<LayoutTemplate> list = ImmutableList.of();
        return new LayoutTemplates( list );
    }

    public static LayoutTemplates from( final LayoutTemplate... templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

    public static LayoutTemplates from( final Iterable<? extends LayoutTemplate> templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

    public static LayoutTemplates from( final Collection<? extends LayoutTemplate> templates )
    {
        return new LayoutTemplates( ImmutableList.copyOf( templates ) );
    }

    private final static class ToNameFunction
        implements Function<LayoutTemplate, LayoutTemplateName>
    {
        @Override
        public LayoutTemplateName apply( final LayoutTemplate value )
        {
            return value.getName();
        }
    }

    public static Builder newLayoutTemplates()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<LayoutTemplate> list = new ImmutableList.Builder<>();

        public Builder add( LayoutTemplate template )
        {
            this.list.add( template );
            return this;
        }

        public LayoutTemplates build()
        {
            return new LayoutTemplates( this.list.build() );
        }
    }
}
