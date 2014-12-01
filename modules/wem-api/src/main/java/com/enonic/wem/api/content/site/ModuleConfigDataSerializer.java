package com.enonic.wem.api.content.site;


import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.module.ModuleKey;

public class ModuleConfigDataSerializer
{
    public void toData( final ModuleConfig moduleConfig, PropertySet parentSet )
    {
        final PropertySet moduleConfigAsSet = parentSet.addSet( "moduleConfig" );
        moduleConfigAsSet.addString( "moduleKey", moduleConfig.getModule().toString() );
        moduleConfigAsSet.addSet( "config", moduleConfig.getConfig().getRoot().copy( parentSet.getTree() ) );
    }

    ModuleConfig fromData( final PropertySet moduleConfigAsSet )
    {
        return ModuleConfig.newModuleConfig().
            module( ModuleKey.from( moduleConfigAsSet.getString( "moduleKey" ) ) ).
            config( moduleConfigAsSet.getSet( "config" ).toTree() ).
            build();
    }
}
