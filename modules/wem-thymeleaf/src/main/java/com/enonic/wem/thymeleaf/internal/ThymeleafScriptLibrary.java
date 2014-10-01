package com.enonic.wem.thymeleaf.internal;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptLibrary;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;
import com.enonic.wem.thymeleaf.ThymeleafProcessorFactory;

public final class ThymeleafScriptLibrary
    implements ScriptLibrary
{
    private final ThymeleafProcessorFactory processorFactory;

    public ThymeleafScriptLibrary( final ThymeleafProcessorFactory processorFactory )
    {
        this.processorFactory = processorFactory;
    }

    @Override
    public String getName()
    {
        return "view/thymeleaf";
    }

    public String render( final ResourceKey view, final Map<String, Object> params )
    {
        final ThymeleafProcessor processor = this.processorFactory.newProcessor();
        processor.view( view );
        processor.parameters( params );
        return processor.process();
    }

    @Override
    public ScriptLibrary getInstance()
    {
        return this;
    }
}
