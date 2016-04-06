package com.enonic.xp.impl.macro;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;


public class NoFormatMacroProcessor
    implements MacroProcessor
{

    @Override
    public String process( final MacroContext context )
    {
        return context.getBody();
    }
}
