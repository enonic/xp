package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.SiteService;

final class ApiMountVerifier
{
    private ApiMountVerifier()
    {
    }

    static boolean isApiMountedOnSite( final DescriptorKey api, final SiteConfigs siteConfigs, final SiteService siteService,
                                       final boolean mediaApiAutoMount )
    {
        if ( mediaApiAutoMount && ApplicationKey.MEDIA_MOD.equals( api.getApplicationKey() ) )
        {
            return true;
        }

        return isListed( api, siteConfigs, siteService );
    }

    private static boolean isListed( final DescriptorKey api, final SiteConfigs siteConfigs, final SiteService siteService )
    {
        return siteConfigs.stream()
            .map( SiteConfig::getApplicationKey )
            .map( siteService::getDescriptor )
            .filter( Objects::nonNull )
            .map( SiteDescriptor::getApiMounts )
            .anyMatch( mounts -> mounts.contains( api ) );
    }
}
