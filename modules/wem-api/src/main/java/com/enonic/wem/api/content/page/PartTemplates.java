package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class PartTemplates
    extends AbstractImmutableEntityList<PartTemplate>
{
    private final ImmutableMap<PartTemplateName, PartTemplate> templatesByName;

    private PartTemplates( final ImmutableList<PartTemplate> list )
    {
        super( list );
        this.templatesByName = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public PartTemplate getTemplate( final PartTemplateName name )
    {
        return this.templatesByName.get( name );
    }

    public static PartTemplates empty()
    {
        final ImmutableList<PartTemplate> list = ImmutableList.of();
        return new PartTemplates( list );
    }

    public static PartTemplates from( final PartTemplate... templates )
    {
        return new PartTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PartTemplates from( final Iterable<? extends PartTemplate> templates )
    {
        return new PartTemplates( ImmutableList.copyOf( templates ) );
    }

    public static PartTemplates from( final Collection<? extends PartTemplate> templates )
    {
        return new PartTemplates( ImmutableList.copyOf( templates ) );
    }

    private final static class ToNameFunction
        implements Function<PartTemplate, PartTemplateName>
    {
        @Override
        public PartTemplateName apply( final PartTemplate value )
        {
            return value.getName();
        }
    }

    public static Builder newPartTemplates()
    {
        return new Builder();
    }

    public static class Builder
    {
        private ImmutableList.Builder<PartTemplate> list = new ImmutableList.Builder<>();

        public Builder add( PartTemplate template )
        {
            this.list.add( template );
            return this;
        }

        public PartTemplates build()
        {
            return new PartTemplates( this.list.build() );
        }
    }
}
