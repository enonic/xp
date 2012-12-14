package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.AbstractImmutableEntityList;
import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;

public final class SubTypes
    extends AbstractImmutableEntityList<SubType>
{
    private final ImmutableMap<QualifiedSubTypeName, SubType> map;

    private SubTypes( final ImmutableList<SubType> list )
    {
        super( list );
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public Set<QualifiedSubTypeName> getNames()
    {
        final Collection<QualifiedSubTypeName> names = Collections2.transform( this.list, new ToNameFunction() );
        return ImmutableSet.copyOf( names );
    }

    public SubType getSubType( final QualifiedSubTypeName qualifiedSubTypeName )
    {
        return map.get( qualifiedSubTypeName );
    }

    public static SubTypes empty()
    {
        final ImmutableList<SubType> list = ImmutableList.of();
        return new SubTypes( list );
    }

    public static SubTypes from( final SubType... subTypes )
    {
        return new SubTypes( ImmutableList.copyOf( subTypes ) );
    }

    public static SubTypes from( final Iterable<? extends SubType> subTypes )
    {
        return new SubTypes( ImmutableList.copyOf( subTypes ) );
    }

    public static SubTypes from( final Collection<? extends SubType> subTypes )
    {
        return new SubTypes( ImmutableList.copyOf( subTypes ) );
    }

    private final static class ToNameFunction
        implements Function<SubType, QualifiedSubTypeName>
    {
        @Override
        public QualifiedSubTypeName apply( final SubType value )
        {
            return value.getQualifiedName();
        }
    }
}
