package com.enonic.xp.impl.macro;

import java.util.function.Function;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.macro.Macro;
import com.enonic.xp.macro.MacroService;

@Component(immediate = true)
public final class MacroServiceImpl
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
        return new HtmlMacroEvaluator( text, macroProcessor ).evaluate();
    }
}
