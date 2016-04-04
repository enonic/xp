package com.enonic.xp.portal.impl.url;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.impl.macro.MacroServiceImpl;
import com.enonic.xp.macro.MacroService;

public class HtmlMacroProcessorTest
{
    private HtmlMacroProcessor macroProcessor;

    private MacroService macroService;

    @Before
    public void before()
    {
        macroService = Mockito.mock( MacroService.class );
        macroProcessor = new HtmlMacroProcessor( new MacroServiceImpl() );
    }

    @Test
    public void process()
    {
        final String processedText = macroProcessor.process(
            "<a href=\"[macroNoBody /]\">[macro par1=\"val1\" par2=\"val2\"/]</a> \\[macroName]skip me[/macroName]" );
//        Mockito.verify( macroService ).parse( "[macroNoBody /]" );
        System.out.println( processedText );
    }
}
