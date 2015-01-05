package com.enonic.wem.servlet.internal.dispatch;

import java.util.Comparator;

import com.enonic.xp.web.WebHandler;

final class WebHandlerComparator
    implements Comparator<WebHandler>
{
    @Override
    public int compare( final WebHandler o1, final WebHandler o2 )
    {
        if ( o1.getOrder() > o2.getOrder() )
        {
            return 1;
        }

        if ( o1.getOrder() < o2.getOrder() )
        {
            return -1;
        }

        return 0;
    }
}
