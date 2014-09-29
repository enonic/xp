package com.enonic.wem.xslt.internal;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptLibrary;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

public final class XsltScriptLibrary
    implements ScriptLibrary
{
    private final XsltProcessorFactory processorFactory;

    public XsltScriptLibrary( final XsltProcessorFactory processorFactory )
    {
        this.processorFactory = processorFactory;
    }

    @Override
    public String getName()
    {
        return "view/xslt";
    }

    public String render( final ResourceKey view, final String inputXml, final Map<String, Object> params )
    {
        final XsltProcessor processor = this.processorFactory.newProcessor();
        processor.view( view );
        processor.inputXml( inputXml );
        processor.parameters( params );
        return processor.process();
    }
}
