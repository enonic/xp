package com.enonic.xp.macro;

import java.util.function.Function;

public interface MacroService
{
    Macro parse( String text );

    String evaluateMacros( String text, Function<Macro, String> macroProcessor );

    String postProcessInstructionSerialize( Macro macro );
}
