package com.enonic.xp.core.impl.app;

import org.osgi.framework.Bundle;

public final class ApplicationHelper
{
    private static final String SITE_XML = "site/site.xml";

    public static boolean isApplication( final Bundle bundle )
    {
        return ( bundle.getEntry( SITE_XML ) != null );
    }
}
