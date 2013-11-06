package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class TemplateIds
    extends AbstractImmutableEntityList<TemplateId>
{
    private TemplateIds( final ImmutableList<TemplateId> list )
    {
        super( list );
    }

    public static TemplateIds from( final TemplateId... templateIds )
    {
        return new TemplateIds( ImmutableList.copyOf( templateIds ) );
    }

    public static TemplateIds from( final Iterable<? extends TemplateId> templateIds )
    {
        return new TemplateIds( ImmutableList.copyOf( templateIds ) );
    }

    public static TemplateIds from( final Collection<? extends TemplateId> templateIds )
    {
        return new TemplateIds( ImmutableList.copyOf( templateIds ) );
    }

    public static TemplateIds empty()
    {
        return new TemplateIds( ImmutableList.<TemplateId>of() );
    }
}
