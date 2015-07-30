package com.enonic.xp.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertySet;

@Beta
public class SiteConfigDataSerializer
{
    public void toData( final SiteConfig siteConfig, PropertySet parentSet )
    {
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "applicationKey", siteConfig.getApplicationKey().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );
    }

    SiteConfig fromData( final PropertySet siteConfigAsSet )
    {
        return SiteConfig.create().
            application( ApplicationKey.from( siteConfigAsSet.getString( "applicationKey" ) ) ).
            config( siteConfigAsSet.getSet( "config" ).toTree() ).
            build();
    }
}
