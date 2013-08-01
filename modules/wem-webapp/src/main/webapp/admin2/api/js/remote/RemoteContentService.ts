///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteContentModel.ts' />

module api_remote {

    export var RemoteContentService:RemoteContentServiceInterface;

    export interface RemoteContentServiceInterface {
        content_createOrUpdate (params:api_remote_content.CreateOrUpdateParams,
                                callback:(result:api_remote_content.CreateOrUpdateResult)=>void):void;
        content_list (params:api_remote_content.ListParams, callback:(result:api_remote_content.ListResult)=>void):void;
        content_tree (params:api_remote_content.GetTreeParams, callback:(result:api_remote_content.GetTreeResult)=>void):void;
        content_get (params:api_remote_content.GetParams, callback:(result:api_remote_content.GetResult)=>void):void;
        content_delete (params:api_remote_content.DeleteParams, callback:(result:api_remote_content.DeleteResult)=>void):void;
        content_find (params:api_remote_content.FindParams, callback:(result:api_remote_content.FindResult)=>void):void;
        content_validate (params:api_remote_content.ValidateParams, callback:(result:api_remote_content.ValidateResult)=>void):void;
        binary_create (params, callback):void;
    }

    class RemoteContentServiceImpl extends BaseRemoteService implements RemoteContentServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "content_createOrUpdate", "content_list", "content_tree", "content_get",
                "content_delete", "content_validate", "content_find", "binary_create"
            ];
            super('api_remote.RemoteContentService', methods);
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

        binary_create(params, callback):void {
            console.log(params, callback);
        }
    }

    var remoteContentServiceImpl = new RemoteContentServiceImpl();
    RemoteContentService = remoteContentServiceImpl;
    remoteContentServiceImpl.init();
}
