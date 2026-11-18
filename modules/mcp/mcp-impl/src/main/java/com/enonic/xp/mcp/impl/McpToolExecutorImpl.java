package com.enonic.xp.mcp.impl;

import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.script.ScriptExports;

@NullMarked
@Component(service = McpToolExecutor.class)
public final class McpToolExecutorImpl
    implements McpToolExecutor
{
    private final McpDescriptorServiceImpl descriptorService;

    private final PortalScriptService scriptService;

    @Activate
    public McpToolExecutorImpl( @Reference final McpDescriptorServiceImpl descriptorService,
                                @Reference final PortalScriptService scriptService )
    {
        this.descriptorService = descriptorService;
        this.scriptService = scriptService;
    }

    @Override
    public Map<String, @Nullable Object> execute( final DescriptorKey key, final String localName,
                                                 final Map<String, @Nullable Object> arguments )
    {
        final var request = PortalRequestAccessor.get();
        final var toolRequest = request == null ? new PortalRequest() : request;
        final var previousApplication = toolRequest.getApplicationKey();
        toolRequest.setApplicationKey( key.getApplicationKey() );
        try
        {
            return PortalRequestAccessor.callWith( toolRequest, () -> {
                final var resource = descriptorService.getControllerResourceKey( key );
                if ( !scriptService.hasScript( resource ) )
                {
                    throw new IllegalArgumentException( "Missing MCP controller " + resource );
                }
                return scriptService.execute( resource ).executeBound( exports -> invoke( exports, localName, arguments ) );
            } );
        }
        finally
        {
            toolRequest.setApplicationKey( previousApplication );
        }
    }

    private static Map<String, @Nullable Object> invoke( final ScriptExports exports, final String localName,
                                                         final Map<String, @Nullable Object> arguments )
    {
        if ( !exports.hasMethod( "execute" ) )
        {
            throw new IllegalArgumentException( "MCP controller " + exports.getScript() + " must export execute(name, args)" );
        }
        final var result = exports.executeMethod( "execute", localName, arguments, McpInvocationScope.current() );
        if ( result == null || !result.isObject() || result.isArray() || result.isFunction() )
        {
            throw new IllegalArgumentException( "MCP controller " + exports.getScript() + " must return an object" );
        }
        return result.getMap();
    }
}
