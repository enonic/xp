package com.enonic.wem.api.content.site;


import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;

public class ModuleConfigDataSerializer
{
    public RootDataSet toData( final ModuleConfig moduleConfig )
    {
        final RootDataSet moduleConfigData = new RootDataSet();
        moduleConfigData.addProperty( "moduleKey", Value.newString( moduleConfig.getModule() ) );
        moduleConfigData.addProperty( "config", Value.newData( moduleConfig.getConfig() ) );
        return moduleConfigData;
    }

    ModuleConfig fromData( final RootDataSet moduleConfigData )
    {
        final ModuleKey moduleKey = ModuleKey.from( moduleConfigData.getProperty( "moduleKey", 0 ).getString() );
        final RootDataSet config = moduleConfigData.getProperty( "config", 0 ).getData();
        return ModuleConfig.newModuleConfig().
            module( moduleKey ).
            config( config ).
            build();
    }
}
