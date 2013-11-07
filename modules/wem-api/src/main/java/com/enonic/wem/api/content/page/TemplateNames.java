package com.enonic.wem.api.content.page;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public class TemplateNames
    extends AbstractImmutableEntityList<TemplateName>
{
    private TemplateNames( final ImmutableList<TemplateName> list )
    {
        super( list );
    }

    public static TemplateNames from( final TemplateName... templateNames )
    {
        return new TemplateNames( ImmutableList.copyOf( templateNames ) );
    }

    public static TemplateNames from( final Iterable<? extends TemplateName> templateNames )
    {
        return new TemplateNames( ImmutableList.copyOf( templateNames ) );
    }

    public static TemplateNames from( final Collection<? extends TemplateName> templateNames )
    {
        return new TemplateNames( ImmutableList.copyOf( templateNames ) );
    }

    public static TemplateNames empty()
    {
        return new TemplateNames( ImmutableList.<TemplateName>of() );
    }
}
