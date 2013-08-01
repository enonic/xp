///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteContentTypeModel.ts' />

module api_remote {

    export var RemoteContentTypeService:RemoteContentTypeServiceInterface;

    export interface RemoteContentTypeServiceInterface {
        contentType_get (params:api_remote_contenttype.GetParams, callback:(result:api_remote_contenttype.GetResult)=>void):void;
        contentType_list (params:api_remote_contenttype.ListParams, callback:(result:api_remote_contenttype.ListResult)=>void):void;
        contentType_createOrUpdate (params:api_remote_contenttype.CreateOrUpdateParams,
                                    callback:(result:api_remote_contenttype.CreateOrUpdateResult)=>void):void;
        contentType_delete (params:api_remote_contenttype.DeleteParams, callback:(result:api_remote_contenttype.DeleteResult)=>void):void;
        contentType_tree (params:api_remote_contenttype.GetTreeParams, callback:(result:api_remote_contenttype.GetTreeResult)=>void):void;
    }

    class RemoteContentTypeServiceImpl extends BaseRemoteService implements RemoteContentTypeServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "contentType_get", "contentType_list", "contentType_createOrUpdate",
                "contentType_delete", "contentType_tree"
            ];
            super('api_remote.RemoteContentTypeService', methods);
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
    }

    var remoteContentTypeServiceImpl = new RemoteContentTypeServiceImpl();
    RemoteContentTypeService = remoteContentTypeServiceImpl;
    remoteContentTypeServiceImpl.init();
}
