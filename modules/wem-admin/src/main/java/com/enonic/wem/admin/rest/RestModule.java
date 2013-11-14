package com.enonic.wem.admin.rest;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandlerBinder;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rest.service.upload.UploadServiceImpl;
import com.enonic.wem.admin.rpc.account.ChangePasswordRpcHandler;
import com.enonic.wem.admin.rpc.account.CreateOrUpdateAccountRpcHandler;
import com.enonic.wem.admin.rpc.account.DeleteAccountsRpcHandler;
import com.enonic.wem.admin.rpc.account.FindAccountsRpcHandler;
import com.enonic.wem.admin.rpc.account.GetAccountGraphRpcHandler;
import com.enonic.wem.admin.rpc.account.GetAccountRpcHandler;
import com.enonic.wem.admin.rpc.account.SuggestUserNameRpcHandler;
import com.enonic.wem.admin.rpc.account.VerifyUniqueEmailRpcHandler;
import com.enonic.wem.admin.rpc.relationship.CreateRelationshipRpcHandler;
import com.enonic.wem.admin.rpc.relationship.GetRelationshipRpcHandler;
import com.enonic.wem.admin.rpc.relationship.UpdateRelationshipPropertiesRpcHandler;
import com.enonic.wem.admin.rpc.space.CreateOrUpdateSpaceRpcHandler;
import com.enonic.wem.admin.rpc.space.DeleteSpaceRpcHandler;
import com.enonic.wem.admin.rpc.space.GetSpaceRpcHandler;
import com.enonic.wem.admin.rpc.space.ListSpacesRpcHandler;
import com.enonic.wem.admin.rpc.system.GetSystemInfoRpcHandler;
import com.enonic.wem.admin.rpc.userstore.CreateOrUpdateUserStoreRpcHandler;
import com.enonic.wem.admin.rpc.userstore.DeleteUserStoreRpcHandler;
import com.enonic.wem.admin.rpc.userstore.GetAllUserStoresRpcHandler;
import com.enonic.wem.admin.rpc.userstore.GetUserStoreRpcHandler;
import com.enonic.wem.admin.rpc.userstore.GetUserstoreConnectorsRpcHandler;
import com.enonic.wem.admin.rpc.util.GetCountriesRpcHandler;
import com.enonic.wem.admin.rpc.util.GetLocalesRpcHandler;
import com.enonic.wem.admin.rpc.util.GetTimeZonesRpcHandler;

public final class RestModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( UploadService.class ).to( UploadServiceImpl.class ).in( Scopes.SINGLETON );

        final JsonRpcHandlerBinder handlers = JsonRpcHandlerBinder.from( binder() );
        handlers.add( ChangePasswordRpcHandler.class );
        handlers.add( CreateOrUpdateAccountRpcHandler.class );
        handlers.add( DeleteAccountsRpcHandler.class );
        handlers.add( FindAccountsRpcHandler.class );
        handlers.add( GetAccountGraphRpcHandler.class );
        handlers.add( GetAccountRpcHandler.class );
        handlers.add( SuggestUserNameRpcHandler.class );
        handlers.add( VerifyUniqueEmailRpcHandler.class );

        handlers.add( GetCountriesRpcHandler.class );
        handlers.add( GetLocalesRpcHandler.class );
        handlers.add( GetTimeZonesRpcHandler.class );

        handlers.add( CreateOrUpdateUserStoreRpcHandler.class );
        handlers.add( DeleteUserStoreRpcHandler.class );
        handlers.add( GetAllUserStoresRpcHandler.class );
        handlers.add( GetUserstoreConnectorsRpcHandler.class );
        handlers.add( GetUserStoreRpcHandler.class );

        handlers.add( GetSystemInfoRpcHandler.class );

        handlers.add( CreateOrUpdateSpaceRpcHandler.class );
        handlers.add( DeleteSpaceRpcHandler.class );
        handlers.add( GetSpaceRpcHandler.class );
        handlers.add( ListSpacesRpcHandler.class );

        handlers.add( CreateRelationshipRpcHandler.class );
        handlers.add( GetRelationshipRpcHandler.class );
        handlers.add( UpdateRelationshipPropertiesRpcHandler.class );
    }
}
