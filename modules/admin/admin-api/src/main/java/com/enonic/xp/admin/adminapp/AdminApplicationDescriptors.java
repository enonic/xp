package com.enonic.xp.admin.adminapp;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class AdminApplicationDescriptors
    extends AbstractImmutableEntityList<AdminApplicationDescriptor>
{

    private AdminApplicationDescriptors( final ImmutableList<AdminApplicationDescriptor> list )
    {
        super( list );
    }

    public static AdminApplicationDescriptors empty()
    {
        final ImmutableList<AdminApplicationDescriptor> list = ImmutableList.of();
        return new AdminApplicationDescriptors( list );
    }

    public static AdminApplicationDescriptors from( final AdminApplicationDescriptor... adminApplicationDescriptors )
    {
        return new AdminApplicationDescriptors( ImmutableList.copyOf( adminApplicationDescriptors ) );
    }

    public static AdminApplicationDescriptors from( final Iterable<? extends AdminApplicationDescriptor> adminApplicationDescriptors )
    {
        return new AdminApplicationDescriptors( ImmutableList.copyOf( adminApplicationDescriptors ) );
    }

    public static AdminApplicationDescriptors from( final Collection<? extends AdminApplicationDescriptor> adminApplicationDescriptors )
    {
        return new AdminApplicationDescriptors( ImmutableList.copyOf( adminApplicationDescriptors ) );
    }

}
