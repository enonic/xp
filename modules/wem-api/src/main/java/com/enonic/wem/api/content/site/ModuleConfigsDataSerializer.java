package com.enonic.wem.api.content.site;


import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;

public class ModuleConfigsDataSerializer
{
    private ModuleConfigDataSerializer moduleConfigSerializer = new ModuleConfigDataSerializer();

    List<Property> toData( final ModuleConfigs moduleConfigs )
    {
        final List<Property> list = new ArrayList<>();
        for ( final ModuleConfig moduleConfig : moduleConfigs )
        {
            list.add( new Property( "modules", Value.newData( moduleConfigSerializer.toData( moduleConfig ) ) ) );
        }
        return list;
    }

    ModuleConfigs.Builder fromData( final DataSet data )
    {
        final ModuleConfigs.Builder builder = ModuleConfigs.builder();
        for ( final Property moduleProperty : data.getPropertiesByName( "modules" ) )
        {
            final RootDataSet moduleConfigData = moduleProperty.getData();
            if ( moduleConfigData != null )
            {
                final ModuleConfig moduleConfig = moduleConfigSerializer.fromData( moduleConfigData );
                builder.add( moduleConfig );
            }
        }
        return builder;
    }

    void addToData( final ModuleConfig moduleConfig, final DataSet data )
    {
        data.addProperty( "modules", Value.newData( moduleConfigSerializer.toData( moduleConfig ) ) );
    }
}
