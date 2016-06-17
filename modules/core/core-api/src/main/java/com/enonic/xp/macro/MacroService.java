package com.enonic.xp.macro;

import java.util.function.Function;

import com.google.common.annotations.Beta;

@Beta
public interface MacroService
{
    Macro parse( String text );

    String evaluateMacros( String text, Function<Macro, String> macroProcessor );

    String postProcessInstructionSerialize( Macro macro );
}
