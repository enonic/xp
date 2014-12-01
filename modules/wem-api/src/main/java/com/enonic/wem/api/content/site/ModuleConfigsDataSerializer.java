package com.enonic.wem.api.content.site;


import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;

public class ModuleConfigsDataSerializer
{
    private ModuleConfigDataSerializer moduleConfigSerializer = new ModuleConfigDataSerializer();

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

    ModuleConfigs.Builder fromProperties( final PropertySet data )
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
