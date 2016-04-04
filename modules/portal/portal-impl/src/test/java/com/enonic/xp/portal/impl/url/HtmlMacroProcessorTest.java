package com.enonic.xp.portal.impl.url;

import org.mockito.Mockito;

import com.enonic.xp.macro.MacroService;

public class HtmlMacroProcessorTest
{
    private HtmlMacroProcessor macroProcessor;

    private MacroService macroService;

    public void before()
    {
        macroService = Mockito.mock( MacroService.class );
        macroProcessor = new HtmlMacroProcessor( macroService );
    }

    public void process()
    {
        final String processedText = macroProcessor.process(
            "<a href=\"[macroNoBody /]\">[macro par1=\"val1\" par2=\"val2\"/]</a> \\[macroName]skip me[/macroName]" );
        Mockito.verify( macroService ).parse( "[macroNoBody /]" );
    }
}
