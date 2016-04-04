package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroService;

public class MacroServiceImpl
    implements MacroService
{
    public Macro parse( final String text )
    {
        return new MacroParser().parse( text );
    }

    @Override
    public String postProcessInstructionSerialize( final Macro macro )
    {
        return new MacroPostProcessInstructionSerializer().serialize( macro );
    }
}
