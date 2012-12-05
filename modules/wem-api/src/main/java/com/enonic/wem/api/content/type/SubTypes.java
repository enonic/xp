package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.wem.api.content.type.form.QualifiedSubTypeName;
import com.enonic.wem.api.content.type.form.SubType;

public final class SubTypes
    implements Iterable<SubType>
{
    private final ImmutableList<SubType> list;

    private final ImmutableMap<QualifiedSubTypeName, SubType> map;

    private SubTypes( final ImmutableList<SubType> list )
    {
        this.list = list;
        this.map = Maps.uniqueIndex( list, new ToNameFunction() );
    }

    public int getSize()
    {
        return this.list.size();
    }

    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    public SubType getFirst()
    {
        return this.list.isEmpty() ? null : this.list.get( 0 );
    }

    public List<SubType> getList()
    {
        return this.list;
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

    @Override
    public Iterator<SubType> iterator()
    {
        return this.list.iterator();
    }

    public int hashCode()
    {
        return this.list.hashCode();
    }

    public boolean equals( final Object o )
    {
        return ( o instanceof SubTypes ) && this.list.equals( ( (SubTypes) o ).list );
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
