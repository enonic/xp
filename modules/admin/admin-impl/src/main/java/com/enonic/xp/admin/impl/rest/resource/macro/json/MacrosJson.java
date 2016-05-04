package com.enonic.xp.admin.impl.rest.resource.macro.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;

public class MacrosJson
{

    private List<MacroDescriptorJson> macros = Lists.newArrayList();

    public MacrosJson( final MacroDescriptors macroDescriptors, final MacroIconUrlResolver macroIconUrlResolver )
    {
        for ( final MacroDescriptor macroDescriptor : macroDescriptors )
        {
            macros.add( new MacroDescriptorJson( macroDescriptor, macroIconUrlResolver ) );
        }
    }

    public List<MacroDescriptorJson> getMacros()
    {
        return macros;
    }
}
