package com.enonic.xp.content.site;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;

@Beta
public class ModuleConfigsDataSerializer
{
    private final ModuleConfigDataSerializer moduleConfigSerializer = new ModuleConfigDataSerializer();

    public void toProperties( final ModuleConfigs moduleConfigs, final PropertySet parentSet )
    {
        for ( final ModuleConfig moduleConfig : moduleConfigs )
        {
            moduleConfigSerializer.toData( moduleConfig, parentSet );
        }
    }

    void toProperties( final ModuleConfig moduleConfig, final PropertySet parentSet )
    {
        moduleConfigSerializer.toData( moduleConfig, parentSet );
    }

    public ModuleConfigs.Builder fromProperties( final PropertySet data )
    {
        final ModuleConfigs.Builder builder = ModuleConfigs.builder();
        for ( final Property moduleConfigAsProperty : data.getProperties( "moduleConfig" ) )
        {
            final ModuleConfig moduleConfig = moduleConfigSerializer.fromData( moduleConfigAsProperty.getSet() );
            builder.add( moduleConfig );
        }
        return builder;
    }
}
