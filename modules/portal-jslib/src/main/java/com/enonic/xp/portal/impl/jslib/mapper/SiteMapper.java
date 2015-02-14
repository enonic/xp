package com.enonic.xp.portal.impl.jslib.mapper;

import com.enonic.xp.content.site.ModuleConfig;
import com.enonic.xp.content.site.ModuleConfigs;
import com.enonic.xp.content.site.Site;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

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
        final ModuleConfigs moduleConfigs = this.site.getModuleConfigs();
        serializeModuleConfigs( gen, moduleConfigs != null ? moduleConfigs : ModuleConfigs.empty() );
    }

    private void serializeModuleConfigs( final MapGenerator gen, final ModuleConfigs moduleConfigs )
    {
        gen.map( "moduleConfigs" );
        for ( ModuleConfig moduleConfig : moduleConfigs )
        {
            serializeModuleConfig( gen, moduleConfig );
        }
        gen.end();
    }

    private void serializeModuleConfig( final MapGenerator gen, final ModuleConfig moduleConfig )
    {
        gen.map( moduleConfig.getModule().toString() );
        new PropertyTreeMapper( moduleConfig.getConfig() ).serialize( gen );
        gen.end();
    }
}
