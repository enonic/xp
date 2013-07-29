///<reference path='RemoteBaseModel.ts' />
///<reference path='RemoteAccountModel.ts' />
///<reference path='RemoteContentTypeModel.ts' />
///<reference path='RemoteContentModel.ts' />
///<reference path='RemoteSpaceModel.ts' />
///<reference path='RemoteMixinModel.ts' />
///<reference path='RemoteRelationshipTypeModel.ts' />
///<reference path='RemoteSchemaModel.ts' />
///<reference path='RemoteUtilsModel.ts' />
///<reference path='RemoteUserStoreModel.ts' />

module api_remote {

    export var RemoteService:RemoteServiceInterface;

    export interface ResultBase {
        success: bool;
        error?: string;
    }

    export interface RemoteServiceInterface {
        account_find (params:api_remote_account.FindParams, callback:(result:api_remote_account.FindResult)=>void):void;
        account_getGraph (params:api_remote_account.GetGraphParams, callback:(result:api_remote_account.GetGraphResult)=>void):void;
        account_changePassword (params:api_remote_account.ChangePasswordParams,
                                callback:(result:api_remote_account.ChangePasswordResult)=>void):void;
        account_verifyUniqueEmail (params:api_remote_account.VerifyUniqueEmailParams,
                                   callback:(result:api_remote_account.VerifyUniqueEmailResult)=>void):void;
        account_suggestUserName (params:api_remote_account.SuggestUserNameParams,
                                 callback:(result:api_remote_account.SuggestUserNameResult)=>void):void;
        account_createOrUpdate (params:api_remote_account.CreateOrUpdateParams,
                                callback:(result:api_remote_account.CreateOrUpdateResult)=>void):void;
        account_delete (params:api_remote_account.DeleteParams, callback:(result:api_remote_account.DeleteResult)=>void):void;
        account_get (params:api_remote_account.GetParams, callback:(result:api_remote_account.GetResult)=>void):void;
        util_getCountries (params:GetCountriesParams, callback:(result:GetCountriesResult)=>void):void;
        util_getLocales (params:GetLocalesParams, callback:(result:GetLocalesResult)=>void):void;
        util_getTimeZones (params:GetTimeZonesParams, callback:(result:GetTimeZonesResult)=>void):void;
        userstore_getAll (params:api_remote_userstore.GetAllParams, callback:(result:api_remote_userstore.GetAllResult)=>void):void;
        userstore_get (params:api_remote_userstore.GetParams, callback:(result:api_remote_userstore.GetResult)=>void):void;
        userstore_getConnectors (params:api_remote_userstore.GetConnectorsParams, callback:(result:api_remote_userstore.GetConnectorsResult)=>void):void;
        userstore_createOrUpdate (params:api_remote_userstore.CreateOrUpdateParams, callback:(result:api_remote_userstore.CreateOrUpdateResult)=>void):void;
        userstore_delete (params:api_remote_userstore.DeleteParams, callback:(result:api_remote_userstore.DeleteResult)=>void):void;
        content_createOrUpdate (params:api_remote_content.CreateOrUpdateParams,
                                callback:(result:api_remote_content.CreateOrUpdateResult)=>void):void;
        content_list (params:api_remote_content.ListParams, callback:(result:api_remote_content.ListResult)=>void):void;
        content_tree (params:api_remote_content.GetTreeParams, callback:(result:api_remote_content.GetTreeResult)=>void):void;
        content_get (params:api_remote_content.GetParams, callback:(result:api_remote_content.GetResult)=>void):void;
        content_delete (params:api_remote_content.DeleteParams, callback:(result:api_remote_content.DeleteResult)=>void):void;
        content_find (params:api_remote_content.FindParams, callback:(result:api_remote_content.FindResult)=>void):void;
        content_validate (params:api_remote_content.ValidateParams, callback:(result:api_remote_content.ValidateResult)=>void):void;
        contentType_get (params:api_remote_contenttype.GetParams, callback:(result:api_remote_contenttype.GetResult)=>void):void;
        contentType_list (params:api_remote_contenttype.ListParams, callback:(result:api_remote_contenttype.ListResult)=>void):void;
        contentType_createOrUpdate (params:api_remote_contenttype.CreateOrUpdateParams,
                                    callback:(result:api_remote_contenttype.CreateOrUpdateResult)=>void):void;
        contentType_delete (params:api_remote_contenttype.DeleteParams, callback:(result:api_remote_contenttype.DeleteResult)=>void):void;
        contentType_tree (params:api_remote_contenttype.GetTreeParams, callback:(result:api_remote_contenttype.GetTreeResult)=>void):void;
        schema_tree (params:api_remote_schema.GetTreeParams, callback:(result:api_remote_schema.GetTreeResult)=>void):void;
        schema_list (params:api_remote_schema.ListParams, callback:(result:api_remote_schema.ListResult)=>void):void;
        system_getSystemInfo (params:SystemGetSystemInfoParams, callback:(result:SystemGetSystemInfoResult)=>void):void;
        mixin_get (params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void):void;
        mixin_createOrUpdate (params:api_remote_mixin.CreateOrUpdateParams,
                              callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void):void;
        mixin_delete (params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void):void;
        relationshipType_delete (params:api_remote_relationshiptype.DeleteParams,
                                 callback:(result:api_remote_relationshiptype.DeleteResult)=>void):void;
        relationshipType_get (params:api_remote_relationshiptype.GetParams,
                              callback:(result:api_remote_relationshiptype.GetResult)=>void):void;
        relationshipType_createOrUpdate (params:api_remote_relationshiptype.CreateOrUpdateParams,
                                         callback:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void):void;
        space_list (params:api_remote_space.ListParams, callback:(result:api_remote_space.ListResult)=>void):void;
        space_get (params:api_remote_space.GetParams, callback:(result:api_remote_space.GetResult)=>void):void;
        space_delete (params:api_remote_space.DeleteParams, callback:(result:api_remote_space.DeleteResult)=>void):void;
        space_createOrUpdate (params:api_remote_space.CreateOrUpdateParams,
                              callback:(result:api_remote_space.CreateOrUpdateResult)=>void):void;
        binary_create (params, callback):void; // TO BE REMOVED
    }

    class RemoteServiceImpl implements RemoteServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
        }

        public init(namespace:string) {
            var url:string = api_util.getAbsoluteUri("admin/rest/jsonrpc");
            var methods:string[] = [
                "account_find", "account_getGraph", "account_changePassword", "account_verifyUniqueEmail", "account_suggestUserName",
                "account_createOrUpdate", "account_delete", "account_get",
                "util_getCountries", "util_getLocales", "util_getTimeZones",
                "userstore_getAll", "userstore_get", "userstore_getConnectors", "userstore_createOrUpdate", "userstore_delete",
                "content_createOrUpdate", "content_list", "content_tree", "content_get",
                "content_delete", "content_validate", "content_find",
                "contentType_get", "contentType_list", "contentType_createOrUpdate", "contentType_delete", "contentType_tree",
                "schema_list", "schema_tree",
                "system_getSystemInfo",
                "mixin_get", "mixin_createOrUpdate", "mixin_delete",
                "relationshipType_get", "relationshipType_createOrUpdate", "relationshipType_delete",
                "space_list", "space_get", "space_delete", "space_createOrUpdate",
                "binary_create"
            ];
            var jsonRpcProvider = new api_remote.JsonRpcProvider(url, methods, namespace);
            this.provider = Ext.Direct.addProvider(jsonRpcProvider.ext);
        }

        account_find(params:api_remote_account.FindParams, callback:(result:api_remote_account.FindResult)=>void):void {
            console.log(params, callback);
        }

        account_getGraph(params:api_remote_account.GetGraphParams, callback:(result:api_remote_account.GetGraphResult)=>void):void {
            console.log(params, callback);
        }

        account_changePassword(params:api_remote_account.ChangePasswordParams,
                               callback:(result:api_remote_account.ChangePasswordResult)=>void):void {
            console.log(params, callback);
        }

        account_verifyUniqueEmail(params:api_remote_account.VerifyUniqueEmailParams,
                                  callback:(result:api_remote_account.VerifyUniqueEmailResult)=>void):void {
            console.log(params, callback);
        }

        account_suggestUserName(params:api_remote_account.SuggestUserNameParams,
                                callback:(result:api_remote_account.SuggestUserNameResult)=>void):void {
            console.log(params, callback);
        }

        account_createOrUpdate(params:api_remote_account.CreateOrUpdateParams,
                               callback:(result:api_remote_account.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        account_delete(params:api_remote_account.DeleteParams, callback:(result:api_remote_account.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        account_get(params:api_remote_account.GetParams, callback:(result:api_remote_account.GetResult)=>void):void {
            console.log(params, callback);
        }

        util_getCountries(params:GetCountriesParams, callback:(result:GetCountriesResult)=>void):void {
            console.log(params, callback);
        }

        util_getLocales(params:GetLocalesParams, callback:(result:GetLocalesResult)=>void):void {
            console.log(params, callback);
        }

        util_getTimeZones(params:GetTimeZonesParams, callback:(result:GetTimeZonesResult)=>void):void {
            console.log(params, callback);
        }

        userstore_getAll(params:api_remote_userstore.GetAllParams, callback:(result:api_remote_userstore.GetAllResult)=>void):void {
            console.log(params, callback);
        }

        userstore_get(params:api_remote_userstore.GetParams, callback:(result:api_remote_userstore.GetResult)=>void):void {
            console.log(params, callback);
        }

        userstore_getConnectors(params:api_remote_userstore.GetConnectorsParams, callback:(result:api_remote_userstore.GetConnectorsResult)=>void):void {
            console.log(params, callback);
        }

        userstore_createOrUpdate(params:api_remote_userstore.CreateOrUpdateParams, callback:(result:api_remote_userstore.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        userstore_delete(params:api_remote_userstore.DeleteParams, callback:(result:api_remote_userstore.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        content_createOrUpdate(params:api_remote_content.CreateOrUpdateParams,
                               callback:(result:api_remote_content.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        content_list(params:api_remote_content.ListParams, callback:(result:api_remote_content.ListResult)=>void):void {
            console.log(params, callback);
        }

        content_tree(params:api_remote_content.GetTreeParams, callback:(result:api_remote_content.GetTreeResult)=>void):void {
            console.log(params, callback);
        }

        content_get(params:api_remote_content.GetParams, callback:(result:api_remote_content.GetResult)=>void):void {
            console.log(params, callback);
        }

        content_delete(params:api_remote_content.DeleteParams, callback:(result:api_remote_content.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        content_find(params:api_remote_content.FindParams, callback:(result:api_remote_content.FindResult)=>void):void {
            console.log(params, callback);
        }

        content_validate(params:api_remote_content.ValidateParams, callback:(result:api_remote_content.ValidateResult)=>void):void {
            console.log(params, callback);
        }

        contentType_get(params:api_remote_contenttype.GetParams, callback:(result:api_remote_contenttype.GetResult)=>void):void {
            console.log(params, callback);
        }

        contentType_list(params:api_remote_contenttype.ListParams, callback:(result:api_remote_contenttype.ListResult)=>void):void {
            console.log(params, callback);
        }

        contentType_createOrUpdate(params:api_remote_contenttype.CreateOrUpdateParams,
                                   callback:(result:api_remote_contenttype.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        contentType_delete(params:api_remote_contenttype.DeleteParams, callback:(result:api_remote_contenttype.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        contentType_tree(params:api_remote_contenttype.GetTreeParams, callback:(result:api_remote_contenttype.GetTreeResult)=>void):void {
            console.log(params, callback);
        }

        schema_tree(params:api_remote_schema.GetTreeParams, callback:(result:api_remote_schema.GetTreeResult)=>void):void {
            console.log(params, callback);
        }

        schema_list(params:api_remote_schema.ListParams, callback:(result:api_remote_schema.ListResult)=>void):void {
            console.log(params, callback);
        }

        system_getSystemInfo(params:SystemGetSystemInfoParams, callback:(result:SystemGetSystemInfoResult)=>void):void {
            console.log(params, callback);
        }

        mixin_get(params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void):void {
            console.log(params, callback);
        }

        mixin_createOrUpdate(params:api_remote_mixin.CreateOrUpdateParams,
                             callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        mixin_delete(params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_get(params:api_remote_relationshiptype.GetParams,
                             callback:(result:api_remote_relationshiptype.GetResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_createOrUpdate(params:api_remote_relationshiptype.CreateOrUpdateParams,
                                        callback:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_delete(params:api_remote_relationshiptype.DeleteParams,
                                callback:(result:api_remote_relationshiptype.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        space_list(params:api_remote_space.ListParams, callback:(result:api_remote_space.ListResult)=>void):void {
            console.log(params, callback);
        }

        space_get(params:api_remote_space.GetParams, callback:(result:api_remote_space.GetResult)=>void):void {
            console.log(params, callback);
        }

        space_delete(params:api_remote_space.DeleteParams, callback:(result:api_remote_space.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        space_createOrUpdate(params:api_remote_space.CreateOrUpdateParams,
                             callback:(result:api_remote_space.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        binary_create(params, callback):void {
            console.log(params, callback);
        }
    }

    var remoteServiceImpl = new RemoteServiceImpl();
    RemoteService = remoteServiceImpl;
    remoteServiceImpl.init('api_remote.RemoteService');
}
