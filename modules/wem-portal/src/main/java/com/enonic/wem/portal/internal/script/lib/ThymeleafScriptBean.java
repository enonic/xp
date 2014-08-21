package com.enonic.wem.portal.internal.script.lib;

import java.util.Map;

import javax.inject.Inject;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.thymeleaf.ThymeleafProcessor;

public final class ThymeleafScriptBean
{
    @Inject
    protected ThymeleafProcessor viewProcessor;

    public String render( final String name, final Map<String, Object> params )
    {
        final ContextScriptBean service = ContextScriptBean.get();
        final ResourceKey view = ResourceKey.from( service.getModule(), name );
        return this.viewProcessor.process( view, params );
    }
}
