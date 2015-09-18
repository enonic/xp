package com.enonic.xp.portal.impl.controller;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.filter.FilterExecutor;
import com.enonic.xp.portal.impl.mapper.PortalRequestMapper;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.web.HttpMethod;

final class ControllerScriptImpl
    implements ControllerScript
{
    private final ScriptExports scriptExports;

    private final PostProcessor postProcessor;

    private final FilterExecutor filterExecutor;

    public ControllerScriptImpl( final ScriptExports scriptExports, final PostProcessor postProcessor,
                                 final PortalScriptService scriptService )
    {
        this.scriptExports = scriptExports;
        this.postProcessor = postProcessor;
        this.filterExecutor = new FilterExecutor( scriptService );
    }

    @Override
    public PortalResponse execute( final PortalRequest portalRequest )
    {
        PortalRequestAccessor.set( portalRequest );

        try
        {
            return this.postProcessor.processResponse( portalRequest, doExecute( portalRequest ) );
        }
        finally
        {
            PortalRequestAccessor.remove();
        }
    }

    private PortalResponse doExecute( final PortalRequest portalRequest )
    {
        final HttpMethod method = portalRequest.getMethod();
        final boolean isHead = method.equals( HttpMethod.HEAD );
        final String runMethod = isHead ? "get" : method.toString().toLowerCase();

        boolean exists = this.scriptExports.hasMethod( runMethod );
        if ( !exists )
        {
            return new PortalResponseSerializer( null ).serialize();
        }

        final PortalRequestMapper requestMapper = new PortalRequestMapper( portalRequest );
        final ScriptValue result = this.scriptExports.executeMethod( runMethod, requestMapper );

        final PortalResponse response = new PortalResponseSerializer( result ).serialize();

        return applyResponseFilters( portalRequest, response );
    }

    public PortalResponse applyResponseFilters( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        ImmutableList<String> filterNames = portalResponse.getFilters();
        if ( filterNames.isEmpty() )
        {
            return portalResponse;
        }

        PortalResponse filterResponse = portalResponse;
        final Set<String> executedFilters = new HashSet<>();

        while ( !filterNames.isEmpty() )
        {
            final String filterName = filterNames.get( 0 );
            filterNames = filterNames.subList( 1, filterNames.size() );
            if ( executedFilters.contains( filterName ) )
            {
                // skip filter already executed
                continue;
            }

            filterResponse = PortalResponse.create( filterResponse ).clearFilters().filters( filterNames ).build();

            filterResponse = this.filterExecutor.executeResponseFilter( filterName, portalRequest, filterResponse );
            executedFilters.add( filterName );
        }

        return filterResponse;
    }
}
