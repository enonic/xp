package com.enonic.xp.macro;

import java.util.function.Function;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface MacroService
{
    Macro parse( String text );

    String evaluateMacros( String text, Function<Macro, String> macroProcessor );

    String postProcessInstructionSerialize( Macro macro );
}
