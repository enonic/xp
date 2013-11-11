package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class Templates
    extends AbstractImmutableEntityList<Template>
{
    private final ImmutableMap<TemplateName, Template> map;

    private Templates( final ImmutableList<Template> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public ImmutableSet<TemplateName> getNames()
    {
        return map.keySet();
    }

    public Template getTemplate( final TemplateName templateName )
    {
        return map.get( templateName );
    }

    @Override
    public String toString()
    {
        return this.list.toString();
    }

    public static Templates empty()
    {
        final ImmutableList<Template> list = ImmutableList.of();
        return new Templates( list );
    }

    public static Templates from( final Template... templates )
    {
        return new Templates( ImmutableList.copyOf( templates ) );
    }

    public static Templates from( final Iterable<? extends Template> templates )
    {
        return new Templates( ImmutableList.copyOf( templates ) );
    }

    public static Templates from( final Collection<? extends Template> templates )
    {
        return new Templates( ImmutableList.copyOf( templates ) );
    }

    private final static class ToNameFunction
        implements Function<Template, TemplateName>
    {
        @Override
        public TemplateName apply( final Template value )
        {
            return value.getName();
        }
    }
}
