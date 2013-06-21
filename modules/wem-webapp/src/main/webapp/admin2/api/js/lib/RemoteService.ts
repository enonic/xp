module api_remote {

    export var RemoteService:RemoteServiceInterface;

    export interface RemoteCallResultBase {
        success: bool;
        error?: string;
    }

    export interface RemoteCallSpaceListParams {
    }

    export interface RemoteCallSpaceListResult extends RemoteCallResultBase {
        total: number;
        spaces: {
            createdTime:Date;
            deletable:bool;
            displayName:string;
            editable:bool;
            iconUrl:string;
            modifiedTime:Date;
            name:string;
            rootContentId:string;
        }[];
    }

    export interface RemoteCallSpaceGetParams {
        spaceName: string[];
    }

    export interface RemoteCallSpaceGetResult extends RemoteCallResultBase {
        total: number;
        space: {
            createdTime:Date;
            displayName:string;
            iconUrl:string;
            modifiedTime:Date;
            name:string;
            rootContentId:string;
        };
    }

    export interface RemoteCallSpaceCreateOrUpdateParams {
        spaceName:string;
        displayName:string;
        iconReference?:string;
    }

    export interface RemoteCallSpaceCreateOrUpdateResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

    export interface RemoteCallSpaceDeleteParams {
        spaceName:string[];
    }

    export interface RemoteCallSpaceDeleteResult extends RemoteCallResultBase {
        deleted:bool;
        failureReason?:string;
    }

    export interface RemoteServiceInterface {
        account_find (params, callback):void;
        account_getGraph (params, callback):void;
        account_changePassword (params, callback):void;
        account_verifyUniqueEmail (params, callback):void;
        account_suggestUserName (params, callback):void;
        account_createOrUpdate (params, callback):void;
        account_delete (params, callback):void;
        account_get (params, callback):void;
        util_getCountries (params, callback):void;
        util_getLocales (params, callback):void;
        util_getTimeZones (params, callback):void;
        userstore_getAll (params, callback):void;
        userstore_get (params, callback):void;
        userstore_getConnectors (params, callback):void;
        userstore_createOrUpdate (params, callback):void;
        userstore_delete (params, callback):void;
        content_createOrUpdate (params, callback):void;
        contentType_get (params, callback):void;
        content_list (params, callback):void;
        content_tree (params, callback):void;
        content_get (params, callback):void;
        contentType_list (params, callback):void;
        content_delete (params, callback):void;
        content_find (params, callback):void;
        content_validate (params, callback):void;
        contentType_createOrUpdate (params, callback):void;
        contentType_delete (params, callback):void;
        contentType_tree (params, callback):void;
        schema_tree (params, callback):void;
        schema_list (params, callback):void;
        system_getSystemInfo (params, callback):void;
        mixin_get (params, callback):void;
        mixin_createOrUpdate (params, callback):void;
        mixin_delete (params, callback):void;
        relationshipType_get (params, callback):void;
        relationshipType_createOrUpdate (params, callback):void;
        relationshipType_delete (params, callback):void;
        space_list (params:RemoteCallSpaceListParams, callback:(result:RemoteCallSpaceListResult)=>void):void;
        space_get (params:RemoteCallSpaceGetParams, callback:(result:RemoteCallSpaceGetResult)=>void):void;
        space_delete (params:RemoteCallSpaceDeleteParams, callback:(result:RemoteCallSpaceDeleteResult)=>void):void;
        space_createOrUpdate (params:RemoteCallSpaceCreateOrUpdateParams, callback:(result:RemoteCallSpaceCreateOrUpdateResult)=>void):void;
        binary_create (params, callback):void;
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
                "content_createOrUpdate", "content_list", "contentType_get", "content_tree", "content_get", "contentType_list",
                "content_delete", "content_validate", "content_find",
                "contentType_createOrUpdate", "contentType_delete", "contentType_tree",
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

        account_find(params, callback:(accountFindResult:any)=>void):void {
            console.log(params, callback);
        }

        account_getGraph(params, callback):void {
            console.log(params, callback);
        }

        account_changePassword(params, callback):void {
            console.log(params, callback);
        }

        account_verifyUniqueEmail(params, callback):void {
            console.log(params, callback);
        }

        account_suggestUserName(params, callback):void {
            console.log(params, callback);
        }

        account_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        account_delete(params, callback):void {
            console.log(params, callback);
        }

        account_get(params, callback):void {
            console.log(params, callback);
        }

        util_getCountries(params, callback):void {
            console.log(params, callback);
        }

        util_getLocales(params, callback):void {
            console.log(params, callback);
        }

        util_getTimeZones(params, callback):void {
            console.log(params, callback);
        }

        userstore_getAll(params, callback):void {
            console.log(params, callback);
        }

        userstore_get(params, callback):void {
            console.log(params, callback);
        }

        userstore_getConnectors(params, callback):void {
            console.log(params, callback);
        }

        userstore_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        userstore_delete(params, callback):void {
            console.log(params, callback);
        }

        content_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        contentType_get(params, callback):void {
            console.log(params, callback);
        }

        content_list(params, callback):void {
            console.log(params, callback);
        }

        content_tree(params, callback):void {
            console.log(params, callback);
        }

        content_get(params, callback):void {
            console.log(params, callback);
        }

        contentType_list(params, callback):void {
            console.log(params, callback);
        }

        content_delete(params, callback):void {
            console.log(params, callback);
        }

        content_find(params, callback):void {
            console.log(params, callback);
        }

        content_validate(params, callback):void {
            console.log(params, callback);
        }

        contentType_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        contentType_delete(params, callback):void {
            console.log(params, callback);
        }

        contentType_tree(params, callback):void {
            console.log(params, callback);
        }

        schema_tree(params, callback):void {
            console.log(params, callback);
        }

        schema_list(params, callback):void {
            console.log(params, callback);
        }

        system_getSystemInfo(params, callback):void {
            console.log(params, callback);
        }

        mixin_get(params, callback):void {
            console.log(params, callback);
        }

        mixin_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        mixin_delete(params, callback):void {
            console.log(params, callback);
        }

        relationshipType_get(params, callback):void {
            console.log(params, callback);
        }

        relationshipType_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        relationshipType_delete(params, callback):void {
            console.log(params, callback);
        }

        space_list(params, callback):void {
            console.log(params, callback);
        }

        space_get(params, callback):void {
            console.log(params, callback);
        }

        space_delete(params:RemoteCallSpaceDeleteParams, callback:(result:RemoteCallSpaceDeleteResult)=>void):void {
            console.log(params, callback);
        }

        space_createOrUpdate(params, callback):void {
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
