package com.enonic.xp.portal.impl;

import com.enonic.xp.content.Content;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

public final class SiteHelper
{
    private static final SiteConfigsDataSerializer SITE_CONFIGS_DATA_SERIALIZER = new SiteConfigsDataSerializer();

    private SiteHelper()
    {
    }

    public static String getDescription( final Content content )
    {
        return content != null ? content.getData().getString( "description" ) : null;
    }

    public static SiteConfigs getSiteConfigs( final Content content )
    {
        return content != null ? SITE_CONFIGS_DATA_SERIALIZER.fromProperties( content.getData().getRoot() ).build() : null;
    }
}
