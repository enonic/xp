package com.enonic.xp.portal.impl.filter;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.impl.mapper.PortalResponseMapper;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public final class FilterExecutor
{

    public static final String RESPONSE_FILTER_METHOD = "responseFilter";

    private final PortalScriptService scriptService;

    public FilterExecutor( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    public PortalResponse executeResponseFilter( final String filterName, final PortalRequest request, final PortalResponse response )
    {

        final ResourceKey script = ResourceKey.from( request.getApplicationKey(), "/site/filters/" + filterName + ".js" );
        final ScriptExports filterExports = this.scriptService.execute( script );

        final boolean exists = filterExports.hasMethod( RESPONSE_FILTER_METHOD );
        if ( !exists )
        {
            return response;
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( request );
        final PortalResponseMapper responseMapper = new PortalResponseMapper( response );
        final ScriptValue result = filterExports.executeMethod( RESPONSE_FILTER_METHOD, requestMapper, responseMapper );

        return new PortalResponseSerializer( result ).serialize();
    }

}
