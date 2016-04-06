package com.enonic.xp.impl.macro;

import java.util.function.Function;

import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroService;

public class MacroServiceImpl
    implements MacroService
{

    @Override
    public Macro parse( final String text )
    {
        return new MacroParser().parse( text );
    }

    @Override
    public String postProcessInstructionSerialize( final Macro macro )
    {
        return new MacroPostProcessInstructionSerializer().serialize( macro );
    }

    @Override
    public String evaluateMacros( final String text, final Function<Macro, String> macroProcessor )
    {
        int nextBracket = text.indexOf( '[' );
        if ( nextBracket < 0 )
        {
            return text;
        }

        String input = text;
        final MacroParser parser = new MacroParser();

        while ( nextBracket >= 0 )
        {
            if ( nextBracket > 1 && input.charAt( nextBracket - 1 ) == '\\' )
            {
                nextBracket++; // ignore escaped opening bracket
                continue;
            }

            final Macro macro = parser.parse( input, nextBracket );
            if ( macro != null )
            {
                final String replacement = macroProcessor.apply( macro );
                input = input.substring( 0, nextBracket ) + replacement + input.substring( parser.parsedEndPos() );
                nextBracket = input.indexOf( '[', nextBracket + replacement.length() );
            }
            else
            {
                nextBracket = input.indexOf( '[', nextBracket + 1 );
            }
        }

        return input;
    }
}
