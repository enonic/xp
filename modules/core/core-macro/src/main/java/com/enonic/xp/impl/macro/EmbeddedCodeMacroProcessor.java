package com.enonic.xp.impl.macro;

import org.apache.commons.lang.StringEscapeUtils;

import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.macro.MacroProcessor;


public class EmbeddedCodeMacroProcessor
    implements MacroProcessor
{

    private static final String CODE_WRAPPER_START =
        "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>";

    private static final String CODE_WRAPPER_END = "</code></pre>";

    @Override
    public String process( final MacroContext context )
    {
        if ( context.getBody() == null )
        {
            return null;
        }

        return CODE_WRAPPER_START + StringEscapeUtils.escapeHtml( context.getBody() ) + CODE_WRAPPER_END;
    }
}
