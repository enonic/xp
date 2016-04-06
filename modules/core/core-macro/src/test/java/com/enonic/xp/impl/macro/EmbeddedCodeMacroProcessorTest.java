package com.enonic.xp.impl.macro;


import org.junit.Test;

import com.enonic.xp.macro.MacroContext;

import static org.junit.Assert.*;

public class EmbeddedCodeMacroProcessorTest
{

    @Test
    public void testProcess()
    {
        final EmbeddedCodeMacroProcessor processor = new EmbeddedCodeMacroProcessor();

        final MacroContext macroContext = MacroContext.create().name( "name" ).
            body( "body" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>body</code></pre>",
            processor.process( macroContext ) );
    }

    @Test
    public void testProcessEscapesHtml()
    {
        final EmbeddedCodeMacroProcessor processor = new EmbeddedCodeMacroProcessor();

        final MacroContext macroContext1 = MacroContext.create().name( "name" ).
            body( "<script>alert(\"I am XSS\");</script\"" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>&lt;script&gt;alert(&quot;I am XSS&quot;);&lt;/script&quot;</code></pre>",
            processor.process( macroContext1 ) );

        final MacroContext macroContext2 = MacroContext.create().name( "name" ).
            body( "<tag1><tag2>body</tag2></tag1>" ).
            build();

        assertEquals(
            "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>&lt;tag1&gt;&lt;tag2&gt;body&lt;/tag2&gt;&lt;/tag1&gt;</code></pre>",
            processor.process( macroContext2 ) );
    }
}
