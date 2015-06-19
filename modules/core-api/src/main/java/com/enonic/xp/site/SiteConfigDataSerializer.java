package com.enonic.xp.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.module.ModuleKey;

@Beta
public class SiteConfigDataSerializer
{
    public void toData( final SiteConfig siteConfig, PropertySet parentSet )
    {
        final PropertySet siteConfigAsSet = parentSet.addSet( "siteConfig" );
        siteConfigAsSet.addString( "moduleKey", siteConfig.getModule().toString() );
        siteConfigAsSet.addSet( "config", siteConfig.getConfig().getRoot().copy( parentSet.getTree() ) );
    }

    SiteConfig fromData( final PropertySet siteConfigAsSet )
    {
        return SiteConfig.create().
            module( ModuleKey.from( siteConfigAsSet.getString( "moduleKey" ) ) ).
            config( siteConfigAsSet.getSet( "config" ).toTree() ).
            build();
    }
}
