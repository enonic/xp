package com.enonic.xp.portal.impl.url;

import com.enonic.xp.macro.MacroService;

public final class HtmlMacroProcessor
{
    private MacroService macroService;

    public HtmlMacroProcessor( final MacroService macroService )
    {
        this.macroService = macroService;
    }

    public String process( final String text )
    {
        return macroService.evaluateMacros( text, ( macro ) -> macroService.postProcessInstructionSerialize( macro ) );
    }

}
