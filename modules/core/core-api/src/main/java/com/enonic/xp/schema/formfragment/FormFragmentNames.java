package com.enonic.xp.schema.formfragment;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class FormFragmentNames
    extends AbstractImmutableEntitySet<FormFragmentName>
{
    private static final FormFragmentNames EMPTY = new FormFragmentNames( ImmutableSet.of() );

    private FormFragmentNames( final ImmutableSet<FormFragmentName> list )
    {
        super( list );
    }

    public static FormFragmentNames empty()
    {
        return EMPTY;
    }

    public static FormFragmentNames from( final String... names )
    {
        return from( Arrays.asList( names ) );
    }

    public static FormFragmentNames from( final Collection<String> names )
    {
        return names.stream().map( FormFragmentName::from ).collect( collector() );
    }

    public static FormFragmentNames from( final FormFragmentName... names )
    {
        return fromInternal( ImmutableSet.copyOf( names ) );
    }

    public static FormFragmentNames from( final Iterable<FormFragmentName> names )
    {
        return names instanceof FormFragmentNames m ? m : fromInternal( ImmutableSet.copyOf( names ) );
    }

    private static FormFragmentNames fromInternal( final ImmutableSet<FormFragmentName> names )
    {
        return names.isEmpty() ? EMPTY : new FormFragmentNames( names );
    }

    public static Collector<FormFragmentName, ?, FormFragmentNames> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), FormFragmentNames::fromInternal );
    }
}
