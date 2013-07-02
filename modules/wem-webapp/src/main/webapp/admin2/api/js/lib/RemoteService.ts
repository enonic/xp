///<reference path='RemoteAccountModel.ts' />
///<reference path='RemoteContentTypeModel.ts' />
///<reference path='RemoteContentModel.ts' />
///<reference path='RemoteSpaceModel.ts' />
///<reference path='RemoteMixinModel.ts' />
///<reference path='RemoteSchemaModel.ts' />
///<reference path='RemoteUtilsModel.ts' />
///<reference path='RemoteUserStoreModel.ts' />

module api_remote {

    export var RemoteService:RemoteServiceInterface;

    export interface RemoteCallResultBase {
        success: bool;
        error?: string;
    }

    export interface RemoteCallAccountFindParams {
        key?: string[];
        query?: string;
        start?: number;
        limit?: number;
        userstores?: string[];
        sort?: string;
        dir?: string;
        types?: string[];
    }

    export interface RemoteCallAccountFindResult extends RemoteCallResultBase {
        accounts: Account[];
        facets?: AccountFacet[];
        total?: number;
    }

    export interface RemoteCallAccountGetGraphParams {
        key: string;
    }

    export interface RemoteCallAccountGetGraphResult extends RemoteCallResultBase {
        graph: {
            id: string;
            name: string;
            data: {
                type: string;
                key: string;
                image_uri: string;
                name: string;
            };
            adjacencies?: {
                nodeTo: string;
            }[];
        }[];
    }

    export interface RemoteCallAccountChangePasswordParams {
        key: string;
        password: string;
    }

    export interface RemoteCallAccountChangePasswordResult extends RemoteCallResultBase {
    }

    export interface RemoteCallAccountVerifyUniqueEmailParams {
        userStore: string;
        email: string;
    }

    export interface RemoteCallAccountVerifyUniqueEmailResult extends RemoteCallResultBase {
        emailInUse: bool;
        key: string;
    }

    export interface RemoteCallAccountSuggestUserNameParams {
        userStore: string;
        firstName: string;
        lastName: string;
    }

    export interface RemoteCallAccountSuggestUserNameResult extends RemoteCallResultBase {
        username: string;
    }

    export interface RemoteCallAccountCreateOrUpdateParams {
        key: string;
        email?: string;
        imageRef?: string;
        profile?: UserProfile;
        members?: string[];
        displayName: string;
        groups?: string[];
    }

    export interface RemoteCallAccountCreateOrUpdateResult extends RemoteCallResultBase {
        created: bool;
        updated: bool;
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

    export interface RemoteCallContentValidateParams {
        qualifiedContentTypeName: string;
        contentData: ContentData;
    }

    export interface RemoteCallContentValidateResult extends RemoteCallResultBase {
        hasError: bool;
        errors: {
            path: string;
            message: string;
        }[];
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

    export interface RemoteCallSchemaListParams {
        types:string[];
        search:string;
        modules:string[];
    }

    export interface RemoteCallSchemaListResult extends RemoteCallResultBase {
        schemas:Schema[];
    }

    export interface RemoteCallSystemGetSystemInfoParams {
    }

    export interface RemoteCallSystemGetSystemInfoResult extends RemoteCallResultBase {
        installationName: string;
        version: string;
        title: string;
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

    export interface RemoteCallGetSchemaTreeParams {
        types:string[];
    }

    export interface RemoteCallGetSchemaTreeResult extends RemoteCallResultBase {
        schemas:SchemaTreeNode[];
        total:number;
    }

    export interface RemoteCallMixinDeleteParams {
        qualifiedMixinNames:string[];
    }

    export interface RemoteCallMixinDeleteResult extends RemoteCallResultBase {
        successes: {
            qualifiedMixinName:string;
        }[];
        failures: {
            qualifiedMixinName:string;
            reason:string;
        }[];
    }

    export interface RemoteCallMixinCreateOrUpdateParams {
        mixin:string;
        iconReference:string;
    }

    export interface RemoteCallMixinCreateOrUpdateResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

    export interface RemoteCallGetContentTreeParams {
        contentIds?:string[];
    }

    export interface RemoteCallGetContentTreeResult extends RemoteCallResultBase {
        total:number;
        contents:ContentTreeNode[];
    }

    export interface RemoteCallDeleteRelationshipTypeParams {
        qualifiedRelationshipTypeNames:string[];
    }

    export interface DeleteRelationshipTypeSuccess {
        qualifiedRelationshipTypeName:string;
    }

    export interface DeleteRelationshipTypeFailure {
        qualifiedRelationshipTypeName:string;
        reason:string;
    }

    export interface RemoteCallDeleteRelationshipTypeResult extends RemoteCallResultBase {
        successes:DeleteRelationshipTypeSuccess[];
        failures:DeleteRelationshipTypeFailure[];
    }

    export interface RemoteCallGetRelationshipTypeParams {
        qualifiedRelationshipTypeName:string;
        format:string;
    }

    export interface RemoteCallGetRelationshipTypeResult extends RemoteCallResultBase {
        iconUrl:string;
        relationshipType:RelationshipType;
    }

    export interface RemoteCallCreateOrUpdateRelationshipTypeParams {
        relationshipType:string;
        iconReference:string;
    }

    export interface RemoteCallCreateOrUpdateRelationshipTypeResult extends RemoteCallResultBase {
        created:bool;
        updated:bool;
    }

    export interface RemoteCallDeleteAccountParams {
        key:string[];
    }

    export interface RemoteCallDeleteAccountResult extends RemoteCallResultBase {
        deleted:number;
    }

    export interface RemoteCallGetAccountParams {
        key:string;
    }

    export interface RemoteCallGetAccountResult extends RemoteCallResultBase, Account {

    }

    export interface RemoteCallGetCountriesParams {
    }

    export interface RemoteCallGetCountriesResult extends RemoteCallResultBase {
        total: number;
        countries: Country[];
    }

    export interface RemoteCallGetLocalesParams {
    }

    export interface RemoteCallGetLocalesResult extends RemoteCallResultBase {
        total: number;
        locales: Locale[];
    }

    export interface RemoteCallGetTimeZonesParams {
    }

    export interface RemoteCallGetTimeZonesResult extends RemoteCallResultBase {
        total: number;
        timezones: TimeZone[];
    }

    export interface RemoteCallUserStoreGetAllParams {
    }

    export interface RemoteCallUserStoreGetAllResult extends RemoteCallResultBase {
        total: number;
        userStores: UserStore[];
    }

    export interface RemoteCallUserStoreGetParams {
        name: string;
    }

    export interface RemoteCallUserStoreGetResult extends UserStore {
        success: bool;
        error?: string;
    }

    export interface RemoteCallUserStoreGetConnectorsParams {
    }

    export interface RemoteCallUserStoreGetConnectorsResult extends RemoteCallResultBase {
        total: number;
        userStoreConnectors: UserStoreConnector[];
    }

    export interface RemoteCallUserStoreCreateOrUpdateParams {
        name: string[];
        defaultUserstore: bool;
        configXML: string;
        connectorName: string;
        administrators: string[];
    }

    export interface RemoteCallUserStoreCreateOrUpdateResult extends RemoteCallResultBase {
        created: bool;
        updated: bool;
    }

    export interface RemoteCallUserStoreDeleteParams {
        name: string[];
    }

    export interface RemoteCallUserStoreDeleteResult extends RemoteCallResultBase {
        deleted: number;
    }

    export interface RemoteServiceInterface {
        account_find (params:RemoteCallAccountFindParams, callback:(result:RemoteCallAccountFindResult)=>void):void;
        account_getGraph (params:RemoteCallAccountGetGraphParams, callback:(result:RemoteCallAccountGetGraphResult)=>void):void;
        account_changePassword (params:RemoteCallAccountChangePasswordParams,
                                callback:(result:RemoteCallAccountChangePasswordResult)=>void):void;
        account_verifyUniqueEmail (params:RemoteCallAccountVerifyUniqueEmailParams,
                                   callback:(result:RemoteCallAccountVerifyUniqueEmailResult)=>void):void;
        account_suggestUserName (params:RemoteCallAccountSuggestUserNameParams,
                                 callback:(result:RemoteCallAccountSuggestUserNameResult)=>void):void;
        account_createOrUpdate (params:RemoteCallAccountCreateOrUpdateParams,
                                callback:(result:RemoteCallAccountCreateOrUpdateResult)=>void):void;
        account_delete (params:RemoteCallDeleteAccountParams, callback:(result:RemoteCallDeleteAccountResult)=>void):void;
        account_get (params:RemoteCallGetAccountParams, callback:(result:RemoteCallGetAccountResult)=>void):void;
        util_getCountries (params:RemoteCallGetCountriesParams, callback:(result:RemoteCallGetCountriesResult)=>void):void;
        util_getLocales (params:RemoteCallGetLocalesParams, callback:(result:RemoteCallGetLocalesResult)=>void):void;
        util_getTimeZones (params:RemoteCallGetTimeZonesParams, callback:(result:RemoteCallGetTimeZonesResult)=>void):void;
        userstore_getAll (params:RemoteCallUserStoreGetAllParams, callback:(result:RemoteCallUserStoreGetAllResult)=>void):void;
        userstore_get (params:RemoteCallUserStoreGetParams, callback:(result:RemoteCallUserStoreGetResult)=>void):void;
        userstore_getConnectors (params:RemoteCallUserStoreGetConnectorsParams,
                                 callback:(result:RemoteCallUserStoreGetConnectorsResult)=>void):void;
        userstore_createOrUpdate (params:RemoteCallUserStoreCreateOrUpdateParams,
                                  callback:(result:RemoteCallUserStoreCreateOrUpdateResult)=>void):void;
        userstore_delete (params:RemoteCallUserStoreDeleteParams, callback:(result:RemoteCallUserStoreDeleteResult)=>void):void;
        content_createOrUpdate (params:RemoteCallCreateOrUpdateContentParams,
                                callback:(result:RemoteCallCreateOrUpdateContentResult)=>void):void;
        content_list (params:RemoteCallContentListParams, callback:(result:RemoteCallContentListResult)=>void):void;
        content_tree (params:RemoteCallGetContentTreeParams, callback:(result:RemoteCallGetContentTreeResult)=>void):void;
        content_get (params:RemoteCallContentGetParams, callback:(result:RemoteCallContentGetResult)=>void):void;
        content_delete (params:RemoteCallContentDeleteParams, callback:(result:RemoteCallContentDeleteResult)=>void):void;
        content_find (params:RemoteCallContentFindParams, callback:(result:RemoteCallContentFindResult)=>void):void;
        content_validate (params:RemoteCallContentValidateParams, callback:(result:RemoteCallContentValidateResult)=>void):void;
        contentType_get (params:RemoteCallContentTypeGetParams, callback:(result:RemoteCallContentTypeGetResult)=>void):void;
        contentType_list (params:RemoteCallContentTypeListParams, callback:(result:RemoteCallContentTypeListResult)=>void):void;
        contentType_createOrUpdate (params:RemoteCallContentTypeCreateOrUpdateParams,
                                    callback:(result:RemoteCallContentTypeCreateOrUpdateResult)=>void):void;
        contentType_delete (params:RemoteCallContentTypeDeleteParams, callback:(result:RemoteCallContentTypeDeleteResult)=>void):void;
        contentType_tree (params:RemoteCallGetContentTypeTreeParams, callback:(result:RemoteCallGetContentTypeTreeResult)=>void):void;
        schema_tree (params:RemoteCallGetSchemaTreeParams, callback:(result:RemoteCallGetSchemaTreeResult)=>void):void;
        schema_list (params:RemoteCallSchemaListParams, callback:(result:RemoteCallSchemaListResult)=>void):void;
        system_getSystemInfo (params:RemoteCallSystemGetSystemInfoParams, callback:(result:RemoteCallSystemGetSystemInfoResult)=>void):void;
        mixin_get (params:RemoteCallMixinGetParams, callback:(result:RemoteCallMixinGetResult)=>void):void;
        mixin_createOrUpdate (params:RemoteCallMixinCreateOrUpdateParams, callback:(result:RemoteCallMixinCreateOrUpdateResult)=>void):void;
        mixin_delete (params:RemoteCallMixinDeleteParams, callback:(result:RemoteCallMixinDeleteResult)=>void):void;
        relationshipType_delete (params:RemoteCallDeleteRelationshipTypeParams,
                                 callback:(result:RemoteCallDeleteRelationshipTypeResult)=>void):void;
        relationshipType_get (params:RemoteCallGetRelationshipTypeParams, callback:(result:RemoteCallGetRelationshipTypeResult)=>void):void;
        relationshipType_createOrUpdate (params:RemoteCallCreateOrUpdateRelationshipTypeParams,
                                         callback:(result:RemoteCallCreateOrUpdateRelationshipTypeResult)=>void):void;
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

        account_find(params:RemoteCallAccountFindParams, callback:(result:RemoteCallAccountFindResult)=>void):void {
            console.log(params, callback);
        }

        account_getGraph(params:RemoteCallAccountGetGraphParams, callback:(result:RemoteCallAccountGetGraphResult)=>void):void {
            console.log(params, callback);
        }

        account_changePassword(params:RemoteCallAccountChangePasswordParams,
                               callback:(result:RemoteCallAccountChangePasswordResult)=>void):void {
            console.log(params, callback);
        }

        account_verifyUniqueEmail(params:RemoteCallAccountVerifyUniqueEmailParams,
                                  callback:(result:RemoteCallAccountVerifyUniqueEmailResult)=>void):void {
            console.log(params, callback);
        }

        account_suggestUserName(params:RemoteCallAccountSuggestUserNameParams,
                                callback:(result:RemoteCallAccountSuggestUserNameResult)=>void):void {
            console.log(params, callback);
        }

        account_createOrUpdate(params:RemoteCallAccountCreateOrUpdateParams,
                               callback:(result:RemoteCallAccountCreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        account_delete(params:RemoteCallDeleteAccountParams, callback:(result:RemoteCallDeleteAccountResult)=>void):void {
            console.log(params, callback);
        }

        account_get(params:RemoteCallGetAccountParams, callback:(result:RemoteCallGetAccountResult)=>void):void {
            console.log(params, callback);
        }

        util_getCountries(params:RemoteCallGetCountriesParams, callback:(result:RemoteCallGetCountriesResult)=>void):void {
            console.log(params, callback);
        }

        util_getLocales(params:RemoteCallGetLocalesParams, callback:(result:RemoteCallGetLocalesResult)=>void):void {
            console.log(params, callback);
        }

        util_getTimeZones(params:RemoteCallGetTimeZonesParams, callback:(result:RemoteCallGetTimeZonesResult)=>void):void {
            console.log(params, callback);
        }

        userstore_getAll(params:RemoteCallUserStoreGetAllParams, callback:(result:RemoteCallUserStoreGetAllResult)=>void):void {
            console.log(params, callback);
        }

        userstore_get(params:RemoteCallUserStoreGetParams, callback:(result:RemoteCallUserStoreGetResult)=>void):void {
            console.log(params, callback);
        }

        userstore_getConnectors(params:RemoteCallUserStoreGetConnectorsParams,
                                callback:(result:RemoteCallUserStoreGetConnectorsResult)=>void):void {
            console.log(params, callback);
        }

        userstore_createOrUpdate(params:RemoteCallUserStoreCreateOrUpdateParams,
                                 callback:(result:RemoteCallUserStoreCreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        userstore_delete(params:RemoteCallUserStoreDeleteParams, callback:(result:RemoteCallUserStoreDeleteResult)=>void):void {
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

        content_validate(params:RemoteCallContentValidateParams, callback:(result:RemoteCallContentValidateResult)=>void):void {
            console.log(params, callback);
        }

        contentType_get(params:RemoteCallContentTypeGetParams, callback:(result:RemoteCallContentTypeGetResult)=>void):void {
            console.log(params, callback);
        }

        contentType_list(params:RemoteCallContentTypeListParams, callback:(result:RemoteCallContentTypeListResult)=>void):void {
            console.log(params, callback);
        }

        contentType_createOrUpdate(params:RemoteCallContentTypeCreateOrUpdateParams,
                                   callback:(result:RemoteCallContentTypeCreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        contentType_delete(params:RemoteCallContentTypeDeleteParams, callback:(result:RemoteCallContentTypeDeleteResult)=>void):void {
            console.log(params, callback);
        }

        contentType_tree(params:RemoteCallGetContentTypeTreeParams, callback:(result:RemoteCallGetContentTypeTreeResult)=>void):void {
            console.log(params, callback);
        }

        schema_tree(params:RemoteCallGetSchemaTreeParams, callback:(result:RemoteCallGetSchemaTreeResult)=>void):void {
            console.log(params, callback);
        }

        schema_list(params:RemoteCallSchemaListParams, callback:(result:RemoteCallSchemaListResult)=>void):void {
            console.log(params, callback);
        }

        system_getSystemInfo(params:RemoteCallSystemGetSystemInfoParams, callback:(result:RemoteCallSystemGetSystemInfoResult)=>void):void {
            console.log(params, callback);
        }

        mixin_get(params:RemoteCallMixinGetParams, callback:(result:RemoteCallMixinGetResult)=>void):void {
            console.log(params, callback);
        }

        mixin_createOrUpdate(params:RemoteCallMixinCreateOrUpdateParams, callback:(result:RemoteCallMixinCreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        mixin_delete(params:RemoteCallMixinDeleteParams, callback:(result:RemoteCallMixinDeleteResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_get(params:RemoteCallGetRelationshipTypeParams, callback:(result:RemoteCallGetRelationshipTypeResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_createOrUpdate(params:RemoteCallCreateOrUpdateRelationshipTypeParams,
                                        callback:(result:RemoteCallCreateOrUpdateRelationshipTypeResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_delete(params:RemoteCallDeleteRelationshipTypeParams,
                                callback:(result:RemoteCallDeleteRelationshipTypeResult)=>void):void {
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
