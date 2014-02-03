package com.enonic.wem.admin.rest;

import com.google.inject.AbstractModule;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandlerBinder;
import com.enonic.wem.admin.rpc.relationship.CreateRelationshipRpcHandler;
import com.enonic.wem.admin.rpc.relationship.GetRelationshipRpcHandler;
import com.enonic.wem.admin.rpc.relationship.UpdateRelationshipPropertiesRpcHandler;
import com.enonic.wem.admin.rpc.system.GetSystemInfoRpcHandler;
import com.enonic.wem.admin.rpc.util.GetCountriesRpcHandler;
import com.enonic.wem.admin.rpc.util.GetLocalesRpcHandler;
import com.enonic.wem.admin.rpc.util.GetTimeZonesRpcHandler;

public final class RestModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final JsonRpcHandlerBinder handlers = JsonRpcHandlerBinder.from( binder() );

        handlers.add( GetCountriesRpcHandler.class );
        handlers.add( GetLocalesRpcHandler.class );
        handlers.add( GetTimeZonesRpcHandler.class );

        handlers.add( GetSystemInfoRpcHandler.class );

        handlers.add( CreateRelationshipRpcHandler.class );
        handlers.add( GetRelationshipRpcHandler.class );
        handlers.add( UpdateRelationshipPropertiesRpcHandler.class );
    }
}
