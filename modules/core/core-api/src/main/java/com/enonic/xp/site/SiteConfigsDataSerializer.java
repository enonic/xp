package com.enonic.xp.site;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

@PublicApi
public final class SiteConfigsDataSerializer
{
    private SiteConfigsDataSerializer()
    {
    }

    public static void toData( final SiteConfigs siteConfigs, final PropertySet parentSet )
    {
        for ( final SiteConfig siteConfig : siteConfigs )
        {
            final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
            siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
            siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );
        }
    }

    public static SiteConfigs fromData( final PropertySet data )
    {
        final SiteConfigs.Builder builder = SiteConfigs.create();
        for ( final Property siteConfigAsProperty : data.getProperties( "siteConfig" ) )
        {
            final PropertySet siteConfigSet = siteConfigAsProperty.getSet();

            builder.add( SiteConfig.create()
                             .application( ApplicationKey.from( siteConfigAsProperty.getSet().getString( "applicationKey" ) ) )
                             .config( siteConfigSet.getSet( "config" ).toTree() )
                             .build() );
        }
        return builder.build();
    }
}
