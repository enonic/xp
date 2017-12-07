package com.enonic.xp.admin.impl.rest.resource.macro.json;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.macro.MacroIconUrlResolver;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptors;

public class MacrosJson
{

    private List<MacroDescriptorJson> macros;

    public MacrosJson( final MacroDescriptors macroDescriptors, final MacroIconUrlResolver macroIconUrlResolver )
    {
        List<MacroDescriptorJson> notSortedMacros = Lists.newArrayList();
        if(macroDescriptors != null)
        {
            for ( final MacroDescriptor macroDescriptor : macroDescriptors )
            {
                notSortedMacros.add( new MacroDescriptorJson( macroDescriptor, macroIconUrlResolver ) );
            }
        }
        macros = getSortedMacros( notSortedMacros );
    }

    private List<MacroDescriptorJson> getSortedMacros( List<MacroDescriptorJson> notSortedMacros )
    {
        return notSortedMacros.stream().sorted(
            ( macro1, macro2 ) -> macro1.getDisplayName().compareTo( macro2.getDisplayName() ) ).collect( Collectors.toList() );
    }

    @SuppressWarnings("unused")
    public List<MacroDescriptorJson> getMacros()
    {
        return macros;
    }
}
