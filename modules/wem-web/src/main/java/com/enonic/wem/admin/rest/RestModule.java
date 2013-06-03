package com.enonic.wem.admin.rest;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.admin.json.rpc.JsonRpcHandlerBinder;
import com.enonic.wem.admin.rest.rpc.account.ChangePasswordRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.CreateOrUpdateAccountRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.DeleteAccountsRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.FindAccountsRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.GetAccountGraphRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.GetAccountRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.SuggestUserNameRpcHandler;
import com.enonic.wem.admin.rest.rpc.account.VerifyUniqueEmailRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.CreateAttachmentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.CreateBinaryRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.CreateOrUpdateContentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.DeleteContentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.FindContentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.GenerateContentNameRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.GetContentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.GetContentTreeRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.ListContentRpcHandler;
import com.enonic.wem.admin.rest.rpc.content.ValidateContentDataRpcHandler;
import com.enonic.wem.admin.rest.rpc.relationship.CreateRelationshipRpcHandler;
import com.enonic.wem.admin.rest.rpc.relationship.GetRelationshipRpcHandler;
import com.enonic.wem.admin.rest.rpc.relationship.UpdateRelationshipPropertiesRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.GetSchemaTreeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.ListSchemasRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.CreateOrUpdateContentTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.DeleteContentTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.GetContentTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.GetContentTypeTreeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.ListContentTypesRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.content.ValidateContentTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.mixin.CreateOrUpdateMixinRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.mixin.DeleteMixinRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.mixin.GetMixinRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.mixin.ListMixinsRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.relationship.CreateOrUpdateRelationshipTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.relationship.DeleteRelationshipTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.relationship.GetRelationshipTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.schema.relationship.ListRelationshipTypeRpcHandler;
import com.enonic.wem.admin.rest.rpc.space.CreateOrUpdateSpaceRpcHandler;
import com.enonic.wem.admin.rest.rpc.space.DeleteSpaceRpcHandler;
import com.enonic.wem.admin.rest.rpc.space.GetSpaceRpcHandler;
import com.enonic.wem.admin.rest.rpc.space.ListSpacesRpcHandler;
import com.enonic.wem.admin.rest.rpc.system.GetSystemInfoRpcHandler;
import com.enonic.wem.admin.rest.rpc.userstore.CreateOrUpdateUserStoreRpcHandler;
import com.enonic.wem.admin.rest.rpc.userstore.DeleteUserStoreRpcHandler;
import com.enonic.wem.admin.rest.rpc.userstore.GetAllUserStoresRpcHandler;
import com.enonic.wem.admin.rest.rpc.userstore.GetUserStoreRpcHandler;
import com.enonic.wem.admin.rest.rpc.userstore.GetUserstoreConnectorsRpcHandler;
import com.enonic.wem.admin.rest.rpc.util.GetCountriesRpcHandler;
import com.enonic.wem.admin.rest.rpc.util.GetLocalesRpcHandler;
import com.enonic.wem.admin.rest.rpc.util.GetTimeZonesRpcHandler;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rest.service.upload.UploadServiceImpl;

public final class RestModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new RestServletModule() );

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

        handlers.add( CreateBinaryRpcHandler.class );
        handlers.add( CreateAttachmentRpcHandler.class );
        handlers.add( CreateOrUpdateContentRpcHandler.class );
        handlers.add( DeleteContentRpcHandler.class );
        handlers.add( FindContentRpcHandler.class );
        handlers.add( GenerateContentNameRpcHandler.class );
        handlers.add( GetContentRpcHandler.class );
        handlers.add( GetContentTreeRpcHandler.class );
        handlers.add( ListContentRpcHandler.class );
        handlers.add( ValidateContentDataRpcHandler.class );

        handlers.add( CreateRelationshipRpcHandler.class );
        handlers.add( GetRelationshipRpcHandler.class );
        handlers.add( UpdateRelationshipPropertiesRpcHandler.class );

        handlers.add( GetSchemaTreeRpcHandler.class );
        handlers.add( ListSchemasRpcHandler.class );

        handlers.add( CreateOrUpdateContentTypeRpcHandler.class );
        handlers.add( DeleteContentTypeRpcHandler.class );
        handlers.add( GetContentTypeRpcHandler.class );
        handlers.add( GetContentTypeTreeRpcHandler.class );
        handlers.add( ListContentTypesRpcHandler.class );
        handlers.add( ValidateContentTypeRpcHandler.class );

        handlers.add( CreateOrUpdateMixinRpcHandler.class );
        handlers.add( DeleteMixinRpcHandler.class );
        handlers.add( GetMixinRpcHandler.class );
        handlers.add( ListMixinsRpcHandler.class );

        handlers.add( CreateOrUpdateRelationshipTypeRpcHandler.class );
        handlers.add( DeleteRelationshipTypeRpcHandler.class );
        handlers.add( GetRelationshipTypeRpcHandler.class );
        handlers.add( ListRelationshipTypeRpcHandler.class );
    }
}
