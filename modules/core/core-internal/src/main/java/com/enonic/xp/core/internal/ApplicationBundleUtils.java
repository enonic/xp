package com.enonic.xp.core.internal;

import org.osgi.framework.Bundle;

public final class ApplicationBundleUtils
{
    private static final String X_BUNDLE_TYPE = "X-Bundle-Type";

    private static final String APPLICATION_BUNDLE_TYPE = "application";

    private static final String SYSTEM_BUNDLE_TYPE = "system";

    private static final String APPLICATION_YML = "application.yml";

    private static final String APPLICATION_YAML = "application.yaml";

    private ApplicationBundleUtils()
    {
    }

    public static boolean isApplication( final Bundle bundle )
    {
        final String bundleType = getBundleType( bundle );
        return APPLICATION_BUNDLE_TYPE.equals( bundleType ) || SYSTEM_BUNDLE_TYPE.equals( bundleType ) ||
            bundle.getEntry( APPLICATION_YML ) != null || bundle.getEntry( APPLICATION_YAML ) != null;
    }

    public static String getApplicationName( final Bundle bundle )
    {
        return bundle.getSymbolicName();
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
