package com.enonic.xp.app;

import org.osgi.framework.Bundle;

public class ApplicationBundleUtils
{
    private static final String X_BUNDLE_TYPE = "X-Bundle-Type";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String SITE_XML = "site/site.xml";

    private static final String APPLICATION_XML = "application.xml";

    public static boolean isApplication( final Bundle bundle )
    {
        final String bundleType = getBundleType( bundle );
        return APPLICATION_BUNDLE_TYPE.equals( bundleType ) || SYSTEM_BUNDLE_TYPE.equals( bundleType ) ||
            bundle.getEntry( APPLICATION_XML ) != null || bundle.getEntry( SITE_XML ) != null;
    }

    public static boolean isSystemApplication( final Bundle bundle )
    {
        final String bundleType = getBundleType( bundle );
        return SYSTEM_BUNDLE_TYPE.equals( bundleType );
    }

    private static String getBundleType( final Bundle bundle )
    {
        return bundle.getHeaders().get( X_BUNDLE_TYPE );
    }
}
