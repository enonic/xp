package com.enonic.xp.site;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

@PublicApi
public final class SiteConfigsDataSerializer
{
    public void toProperties( final SiteConfigs siteConfigs, final PropertySet parentSet )
    {
        for ( final SiteConfig siteConfig : siteConfigs )
        {
            toProperties( siteConfig, parentSet );
        }
    }

    public void toProperties( final SiteConfig siteConfig, PropertySet parentSet )
    {
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );
    }

    public SiteConfigs getConfigs( final PropertySet data )
    {
        final SiteConfigs.Builder builder = SiteConfigs.create();
        for ( final Property siteConfigAsProperty : data.getProperties( "siteConfig" ) )
        {
            final SiteConfig siteConfig = getConfig( siteConfigAsProperty.getSet() );
            builder.add( siteConfig );
        }
        return builder.build();
    }

    public SiteConfig getConfig( final PropertySet siteConfigAsSet )
    {
        return SiteConfig.create()
            .application( ApplicationKey.from( siteConfigAsSet.getString( "applicationKey" ) ) )
            .config( siteConfigAsSet.getSet( "config" ).toTree() )
            .build();
    }
}
