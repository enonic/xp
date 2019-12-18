package com.enonic.xp.admin.tool;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class AdminToolDescriptors
    extends AbstractImmutableEntityList<AdminToolDescriptor>
{

    private AdminToolDescriptors( final ImmutableList<AdminToolDescriptor> list )
    {
        super( list );
    }

    public static AdminToolDescriptors empty()
    {
        return new AdminToolDescriptors( ImmutableList.of() );
    }

    public static AdminToolDescriptors from( final AdminToolDescriptor... adminToolDescriptors )
    {
        return new AdminToolDescriptors( ImmutableList.copyOf( adminToolDescriptors ) );
    }

    public static AdminToolDescriptors from( final Iterable<? extends AdminToolDescriptor> adminToolDescriptors )
    {
        return new AdminToolDescriptors( ImmutableList.copyOf( adminToolDescriptors ) );
    }

    public static AdminToolDescriptors from( final Collection<? extends AdminToolDescriptor> adminToolDescriptors )
    {
        return new AdminToolDescriptors( ImmutableList.copyOf( adminToolDescriptors ) );
    }

}
