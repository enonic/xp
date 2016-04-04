package com.enonic.xp.macro;

public interface MacroService
{
    Macro parse( final String text );

    String postProcessInstructionSerialize( final Macro macro );
}
