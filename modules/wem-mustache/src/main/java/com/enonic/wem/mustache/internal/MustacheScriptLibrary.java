package com.enonic.wem.mustache.internal;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.mustache.MustacheProcessor;
import com.enonic.wem.mustache.MustacheProcessorFactory;
import com.enonic.wem.script.ScriptLibrary;

@Singleton
public final class MustacheScriptLibrary
    implements ScriptLibrary
{
    @Inject
    protected MustacheProcessorFactory processorFactory;

    @Override
    public String getName()
    {
        return "view/mustache";
    }

    public String render( final ResourceKey view, final Map<String, Object> params )
    {
        final MustacheProcessor processor = this.processorFactory.newProcessor();
        processor.view( view );
        processor.parameters( params );
        return processor.process();
    }
}
