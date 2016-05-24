package com.enonic.xp.admin.impl.rest.resource.macro;


import com.enonic.xp.icon.Icon;
import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.macro.MacroDescriptorService;
import com.enonic.xp.macro.MacroKey;

public final class MacroIconResolver
{
    private final MacroDescriptorService macroDescriptorService;

    public MacroIconResolver( final MacroDescriptorService macroDescriptorService )
    {
        this.macroDescriptorService = macroDescriptorService;
    }

    public Icon resolveIcon( final MacroKey macroKey )
    {
        final MacroDescriptor macroDescriptor = macroDescriptorService.getByKey( macroKey );
        return macroDescriptor == null ? null : macroDescriptor.getIcon();
    }

}
