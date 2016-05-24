package com.enonic.xp.portal.impl.macro;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.macro.MacroContext;

@Component(immediate = true, service = BuiltInMacroProcessor.class)
public class EmbeddedCodeMacroProcessor
    implements BuiltInMacroProcessor
{

    private static final String CODE_WRAPPER_START =
        "<pre style=\"background-color: #f8f8f8; border: 1px solid #dfdfdf; white-space: pre-wrap; word-wrap: break-word; margin: 1.5em 0; padding: 0.125rem 0.3125rem 0.0625rem;\"><code>";

    private static final String CODE_WRAPPER_END = "</code></pre>";

    @Override
    public String getName()
    {
        return "code";
    }

    @Override
    public PortalResponse process( final MacroContext context )
    {
        if ( context.getBody() == null )
        {
            return null;
        }

        final String html = CODE_WRAPPER_START + context.getBody() + CODE_WRAPPER_END;
        return PortalResponse.create().body( html ).build();
    }
}
