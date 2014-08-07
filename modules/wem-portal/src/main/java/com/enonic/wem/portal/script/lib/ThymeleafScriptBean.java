package com.enonic.wem.portal.script.lib;

import java.util.Map;

import javax.inject.Inject;

import com.enonic.wem.api.resource.ModuleResourceKey;
import com.enonic.wem.portal.view.RenderViewSpec;
import com.enonic.wem.portal.view.ViewService;

public final class ThymeleafScriptBean
{
    @Inject
    protected ViewService viewService;

    public String render( final String name, final Map<String, Object> params )
    {
        final ContextScriptBean service = ContextScriptBean.get();

        final ModuleResourceKey view = ModuleResourceKey.from( service.getModule(), name );
        final RenderViewSpec spec = new RenderViewSpec().processor( "thymeleaf" ).params( params ).view( view );
        return this.viewService.renderView( spec );
    }
}
