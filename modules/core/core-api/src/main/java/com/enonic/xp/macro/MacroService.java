package com.enonic.xp.macro;

import java.util.function.Function;


public interface MacroService
{
    Macro parse( String text );

    String evaluateMacros( String text, Function<Macro, String> macroProcessor );

    /**
     * Serializes a macro as a post-processing instruction.
     *
     * @deprecated post-processing executes only instructions naming the macro descriptor they were resolved to, which
     * {@code PortalUrlService.processHtml} writes for the macros of the text it processes. Use it instead.
     */
    @Deprecated
    String postProcessInstructionSerialize( Macro macro );
}
