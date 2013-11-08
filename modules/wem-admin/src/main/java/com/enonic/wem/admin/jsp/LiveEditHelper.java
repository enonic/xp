package com.enonic.wem.admin.jsp;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Only used for live edit testing.
 */
public final class LiveEditHelper
{
    @Inject
    private static Injector INJECTOR;

    public static LiveEditService getService()
    {
        return INJECTOR.getInstance( LiveEditService.class );
    }
}
