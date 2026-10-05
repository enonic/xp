package com.enonic.xp.macro;

import java.util.function.Function;


public interface MacroService
{
    Macro parse( String text );

    String evaluateMacros( String text, Function<Macro, String> macroProcessor );

    /**
     * Turns a macro into an HTML comment that XP replaced with the output of the macro when rendering the page.
     *
     * @deprecated XP no longer replaces the comments this method writes: the page shows the macro as written instead.
     * Pass rich text through {@code PortalUrlService.processHtml}; the macros it finds are rendered with the page.
     */
    @Deprecated
    String postProcessInstructionSerialize( Macro macro );
}
