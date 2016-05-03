package com.enonic.xp.admin.impl.rest.resource.macro.json;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;

public class MacrosJson
{

    private List<MacroDescriptorJson> macros = Lists.newArrayList();

    public MacrosJson( final MacroDescriptors macroDescriptors )
    {
        for ( final MacroDescriptor macroDescriptor : macroDescriptors )
        {
            macros.add( new MacroDescriptorJson( macroDescriptor ) );
        }
    }

    public List<MacroDescriptorJson> getMacros()
    {
        return macros;
    }
}
