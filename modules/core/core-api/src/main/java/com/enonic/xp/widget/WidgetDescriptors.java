package com.enonic.xp.widget;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class WidgetDescriptors
    extends AbstractImmutableEntityList<WidgetDescriptor>
{

    private WidgetDescriptors( final ImmutableList<WidgetDescriptor> list )
    {
        super( list );
    }

    public static WidgetDescriptors empty()
    {
        final ImmutableList<WidgetDescriptor> list = ImmutableList.of();
        return new WidgetDescriptors( list );
    }

    public static WidgetDescriptors from( final WidgetDescriptor... widgetDescriptors )
    {
        return new WidgetDescriptors( ImmutableList.copyOf( widgetDescriptors ) );
    }

    public static WidgetDescriptors from( final Iterable<? extends WidgetDescriptor> widgetDescriptors )
    {
        return new WidgetDescriptors( ImmutableList.copyOf( widgetDescriptors ) );
    }

    public static WidgetDescriptors from( final Collection<? extends WidgetDescriptor> widgetDescriptors )
    {
        return new WidgetDescriptors( ImmutableList.copyOf( widgetDescriptors ) );
    }

}
