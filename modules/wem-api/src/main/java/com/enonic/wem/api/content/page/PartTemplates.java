package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class PartTemplates
    extends AbstractImmutableEntityList<PartTemplate>
{
    private PartTemplates( final ImmutableList<PartTemplate> list )
    {
        super( list );
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

}
