package com.enonic.wem.web.filter.bundle.compressor;

import java.io.StringReader;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

@Component
public final class JsMinScriptCompressor
    implements ScriptCompressor
{
    private final JSMinProcessor processor;

    public JsMinScriptCompressor()
    {
        this.processor = new JSMinProcessor();
    }

    @Override
    public String compress( final String content )
        throws Exception
    {
        final StringWriter writer = new StringWriter();
        this.processor.process( new StringReader( content ), writer );
        return writer.toString();
    }
}
