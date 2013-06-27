///<reference path='RemoteContentTypeModel.ts' />
///<reference path='RemoteContentModel.ts' />
///<reference path='RemoteSpaceModel.ts' />
///<reference path='RemoteMixinModel.ts' />

module api_remote {

    export var RemoteService:RemoteServiceInterface;

    export interface RemoteCallResultBase {
        success: bool;
        error?: string;
    }

    export interface RemoteCallContentTypeGetParams {
        format: string;
        contentType: string;
        mixinReferencesToFormItems?: bool;
    }

    export interface RemoteCallContentTypeGetResult extends RemoteCallResultBase {
        contentType?: ContentType;
        iconUrl?: string;
        contentTypeXml?: string;
    }

    export interface RemoteCallContentTypeCreateOrUpdateParams {
        contentType: string;
        iconReference: string;
    }

    export interface RemoteCallContentTypeCreateOrUpdateResult extends RemoteCallResultBase {
        created: bool;
        updated: bool;
        failure?: string;
    }

    export interface RemoteCallSpaceListParams {
    }

    export interface RemoteCallSpaceListResult extends RemoteCallResultBase {
        total: number;
        spaces: Space[];
    }

    export interface RemoteCallSpaceGetParams {
        spaceName: string[];
    }

    export interface RemoteCallSpaceGetResult extends RemoteCallResultBase {
        space: SpaceSummary;
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

    export interface RemoteCallContentTypeDeleteParams {
        qualifiedContentTypeNames:string[];
    }

    export interface RemoteCallContentTypeDeleteResult extends RemoteCallResultBase {
        successes:RemoteCallContentTypeDeleteSuccess[];
        failures:RemoteCallContentTypeDeleteFailure[];
    }

    export interface RemoteCallContentTypeDeleteSuccess {
        qualifiedContentTypeName:string;

    }

    export interface RemoteCallContentTypeDeleteFailure {
        qualifiedContentTypeName:string;
        reason:string;
    }

    export interface RemoteCallContentGetParams {
        path?: string;
        contentIds?: string[];
    }

    export interface RemoteCallContentGetResult extends RemoteCallResultBase {
        content: ContentGet[];
    }

    export interface RemoteCallContentListParams {
        path: string;
    }

    export interface RemoteCallContentListResult extends RemoteCallResultBase {
        total: number;
        contents: ContentList[];
    }

    export interface RemoteCallGetContentTypeTreeParams {
    }

    export interface RemoteCallGetContentTypeTreeResult extends RemoteCallResultBase {
        total:number;
        contentTypes:ContentTypeTreeNode[];
    }

    export interface RemoteCallContentFindParams {
        fulltext?: string;
        includeFacets?: bool;
        contentTypes: string[];
        spaces?: string[];
        ranges?: {
            lower: string;
            upper: string;
        }[];
        facets: {
            [key:string]:any;
        };
    }

    export interface RemoteCallContentFindResult extends RemoteCallResultBase {
        total: number;
        contents: ContentFind[];
        facets?: ContentFacet[];
    }

    export interface RemoteCallContentDeleteParams {
        contentPaths: string[];
    }

    export interface RemoteCallContentDeleteResult extends RemoteCallResultBase {
        successes: {
            path:string;
        }[];
        failures: {
            path:string;
            reason:string;
        }[];
    }

    export interface RemoteCallContentTypeListParams {
    }

    export interface RemoteCallContentTypeListResult extends RemoteCallResultBase{
        contentTypes:ContentTypeListNode[];
    }

    export interface RemoteCallMixinGetParams {
        format:string;
        mixin:string;
    }

    export interface RemoteCallMixinGetResult extends RemoteCallResultBase {
        mixin?: Mixin;
        mixinXml:string;
        iconUrl:string;
    }

    export interface RemoteCallCreateOrUpdateContentParams {
        contentId?: string;
        temporary?: bool;
        contentName?: string;
        parentContentPath?: string;
        qualifiedContentTypeName: string;
        contentData: {
            [key:string]: string;
        };
        displayName: string;
        attachments?: {
            uploadId: string;
            attachmentName: string;
        }[];
    }

    export interface RemoteCallCreateOrUpdateContentResult extends RemoteCallResultBase{
        created: bool;
        updated: bool;
        contentId?: string;
        contentPath?: string;
        failure?: string;
    }

    export interface RemoteCallGetContentTreeParams {
        contentIds?:string[];
    }

    export interface RemoteCallGetContentTreeResult extends RemoteCallResultBase {
        total:number;
        contents:ContentTreeNode[];
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
        content_createOrUpdate (params:RemoteCallCreateOrUpdateContentParams,
                                callback:(result:RemoteCallCreateOrUpdateContentResult)=>void):void;
        content_list (params:RemoteCallContentListParams, callback:(result:RemoteCallContentListResult)=>void):void;
        content_tree (params:RemoteCallGetContentTreeParams, callback:(result:RemoteCallGetContentTreeResult)=>void):void;
        content_get (params:RemoteCallContentGetParams, callback:(result:RemoteCallContentGetResult)=>void):void;
        content_delete (params:RemoteCallContentDeleteParams, callback:(result:RemoteCallContentDeleteResult)=>void):void;
        content_find (params:RemoteCallContentFindParams, callback:(result:RemoteCallContentFindResult)=>void):void;
        content_validate (params, callback):void;
        contentType_get (params:RemoteCallContentTypeGetParams, callback:(result:RemoteCallContentTypeGetResult)=>void):void;
        contentType_list (params:RemoteCallContentTypeListParams, callback:(result:RemoteCallContentTypeListResult)=>void):void;
        contentType_createOrUpdate (params:RemoteCallContentTypeCreateOrUpdateParams,
                                    callback:(result:RemoteCallContentTypeCreateOrUpdateResult)=>void):void;
        contentType_delete (params:RemoteCallContentTypeDeleteParams, callback:(result:RemoteCallContentTypeDeleteResult)=>void):void;
        contentType_tree (params:RemoteCallGetContentTypeTreeParams, callback:(result:RemoteCallGetContentTypeTreeResult)=>void):void;
        schema_tree (params, callback):void;
        schema_list (params, callback):void;
        system_getSystemInfo (params, callback):void;
        mixin_get (params:RemoteCallMixinGetParams, callback:(result:RemoteCallMixinGetResult)=>void):void;
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

        content_createOrUpdate(params:RemoteCallCreateOrUpdateContentParams,
                               callback:(result:RemoteCallCreateOrUpdateContentResult)=>void):void {
            console.log(params, callback);
        }

        content_list(params:RemoteCallContentListParams, callback:(result:RemoteCallContentListResult)=>void):void {
            console.log(params, callback);
        }

        content_tree(params:RemoteCallGetContentTreeParams, callback:(result:RemoteCallGetContentTreeResult)=>void):void {
            console.log(params, callback);
        }

        content_get(params:RemoteCallContentGetParams, callback:(result:RemoteCallContentGetResult)=>void):void {
            console.log(params, callback);
        }

        content_delete(params:RemoteCallContentDeleteParams, callback:(result:RemoteCallContentDeleteResult)=>void):void {
            console.log(params, callback);
        }

        content_find(params:RemoteCallContentFindParams, callback:(result:RemoteCallContentFindResult)=>void):void {
            console.log(params, callback);
        }

        content_validate(params, callback):void {
            console.log(params, callback);
        }

        contentType_get(params, callback):void {
            console.log(params, callback);
        }

        contentType_list(params:RemoteCallContentTypeListParams, callback:(result:RemoteCallContentTypeListResult)=>void):void {
            console.log(params, callback);
        }

        contentType_createOrUpdate(params, callback):void {
            console.log(params, callback);
        }

        contentType_delete(params:RemoteCallContentTypeDeleteParams, callback:(result:RemoteCallContentTypeDeleteResult)=>void):void {
            console.log(params, callback);
        }

        contentType_tree(params:RemoteCallGetContentTypeTreeParams, callback:(result:RemoteCallGetContentTypeTreeResult)=>void):void {
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

        mixin_get(params:RemoteCallMixinGetParams, callback:(result:RemoteCallMixinGetResult)=>void):void {
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

        space_list(params:RemoteCallSpaceListParams, callback:(result:RemoteCallSpaceListResult)=>void):void {
            console.log(params, callback);
        }

        space_get(params:RemoteCallSpaceGetParams, callback:(result:RemoteCallSpaceGetResult)=>void):void {
            console.log(params, callback);
        }

        space_delete(params:RemoteCallSpaceDeleteParams, callback:(result:RemoteCallSpaceDeleteResult)=>void):void {
            console.log(params, callback);
        }

        space_createOrUpdate(params:RemoteCallSpaceCreateOrUpdateParams, callback:(result:RemoteCallSpaceCreateOrUpdateResult)=>void):void {
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
