package com.enonic.xp.content.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

@Beta
public class SiteConfigsDataSerializer
{
    private final SiteConfigDataSerializer siteConfigSerializer = new SiteConfigDataSerializer();

    public void toProperties( final SiteConfigs siteConfigs, final PropertySet parentSet )
    {
        for ( final SiteConfig siteConfig : siteConfigs )
        {
            siteConfigSerializer.toData( siteConfig, parentSet );
        }
    }

    void toProperties( final SiteConfig siteConfig, final PropertySet parentSet )
    {
        siteConfigSerializer.toData( siteConfig, parentSet );
    }

    public SiteConfigs.Builder fromProperties( final PropertySet data )
    {
        final SiteConfigs.Builder builder = SiteConfigs.builder();
        for ( final Property siteConfigAsProperty : data.getProperties( "moduleConfig" ) )
        {
            final SiteConfig siteConfig = siteConfigSerializer.fromData( siteConfigAsProperty.getSet() );
            builder.add( siteConfig );
        }
        return builder;
    }
}
