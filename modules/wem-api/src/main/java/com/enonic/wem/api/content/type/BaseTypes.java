package com.enonic.wem.api.content.type;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.content.BaseType;
import com.enonic.wem.api.util.AbstractImmutableEntityList;

public final class BaseTypes
    extends AbstractImmutableEntityList<BaseType>
{
    private BaseTypes( final ImmutableList<BaseType> list )
    {
        super( list );
    }

    public static BaseTypes empty()
    {
        final ImmutableList<BaseType> list = ImmutableList.of();
        return new BaseTypes( list );
    }

    public static BaseTypes from( final BaseType... baseTypes )
    {
        return new BaseTypes( ImmutableList.copyOf( baseTypes ) );
    }

    public static BaseTypes from( final Iterable<? extends BaseType>... baseTypes )
    {
        final List<BaseType> all = Lists.newArrayList();
        for ( final Iterable<? extends BaseType> iterable : baseTypes )
        {
            for ( BaseType baseType : iterable )
            {
                all.add( baseType );
            }
        }
        return new BaseTypes( ImmutableList.copyOf( all ) );
    }

    public static BaseTypes from( final Collection<? extends BaseType> baseTypes )
    {
        return new BaseTypes( ImmutableList.copyOf( baseTypes ) );
    }

}
