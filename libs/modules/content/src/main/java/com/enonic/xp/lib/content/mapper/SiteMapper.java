package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;

public final class SiteMapper
    implements MapSerializable
{
    private final Site site;

    public SiteMapper( final Site site )
    {
        this.site = site;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        new ContentMapper( this.site ).serialize( gen );

        gen.value( "description", this.site.getDescription() );
        final SiteConfigs siteConfigs = this.site.getSiteConfigs();
        serializeSiteConfigs( gen, siteConfigs != null ? siteConfigs : SiteConfigs.empty() );
    }

    private void serializeSiteConfigs( final MapGenerator gen, final SiteConfigs siteConfigs )
    {
        gen.map( "siteConfigs" );
        for ( SiteConfig siteConfig : siteConfigs )
        {
            serializeSiteConfig( gen, siteConfig );
        }
        gen.end();
    }

    private void serializeSiteConfig( final MapGenerator gen, final SiteConfig siteConfig )
    {
        gen.map( siteConfig.getModule().toString() );
        new PropertyTreeMapper( siteConfig.getConfig() ).serialize( gen );
        gen.end();
    }
}
