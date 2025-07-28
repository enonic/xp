package com.enonic.xp.admin.tool;

import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;

@PublicApi
public final class AdminToolDescriptors
    extends AbstractImmutableEntityList<AdminToolDescriptor>
{
    private static final AdminToolDescriptors EMPTY = new AdminToolDescriptors( ImmutableList.of() );

    private AdminToolDescriptors( final ImmutableList<AdminToolDescriptor> list )
    {
        super( list );
    }

    public static AdminToolDescriptors empty()
    {
        return EMPTY;
    }

    public static AdminToolDescriptors from( final AdminToolDescriptor... descriptors )
    {
        return fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    public static AdminToolDescriptors from( final Iterable<AdminToolDescriptor> descriptors )
    {
        return descriptors instanceof AdminToolDescriptors d ? d : fromInternal( ImmutableList.copyOf( descriptors ) );
    }

    private static AdminToolDescriptors fromInternal( final ImmutableList<AdminToolDescriptor> list )
    {
        return list.isEmpty() ? EMPTY : new AdminToolDescriptors( list );
    }

    public static Collector<AdminToolDescriptor, ?, AdminToolDescriptors> collector()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), AdminToolDescriptors::fromInternal );
    }
}
