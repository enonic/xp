package com.enonic.xp.site;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

@PublicApi
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
        final SiteConfigs.Builder builder = SiteConfigs.create();
        for ( final Property siteConfigAsProperty : data.getProperties( "siteConfig" ) )
        {
            final SiteConfig siteConfig = siteConfigSerializer.fromData( siteConfigAsProperty.getSet() );
            builder.add( siteConfig );
        }
        return builder;
    }
}
