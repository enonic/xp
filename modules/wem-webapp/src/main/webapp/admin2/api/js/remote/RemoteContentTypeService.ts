///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteContentTypeModel.ts' />

module api_remote {

    export var RemoteContentTypeService:RemoteContentTypeServiceInterface;

    export interface RemoteContentTypeServiceInterface {
        contentType_get (params:api_remote_contenttype.GetParams, success:(result:api_remote_contenttype.GetResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_list (params:api_remote_contenttype.ListParams, success:(result:api_remote_contenttype.ListResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_createOrUpdate (params:api_remote_contenttype.CreateOrUpdateParams,
                                    success:(result:api_remote_contenttype.CreateOrUpdateResult)=>void,
                                    failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_delete (params:api_remote_contenttype.DeleteParams, success:(result:api_remote_contenttype.DeleteResult)=>void,
                            failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_tree (params:api_remote_contenttype.GetTreeParams, success:(result:api_remote_contenttype.GetTreeResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
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

        contentType_get(params:api_remote_contenttype.GetParams, success:(result:api_remote_contenttype.GetResult)=>void,
                        failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        contentType_list(params:api_remote_contenttype.ListParams, success:(result:api_remote_contenttype.ListResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        contentType_createOrUpdate(params:api_remote_contenttype.CreateOrUpdateParams,
                                   success:(result:api_remote_contenttype.CreateOrUpdateResult)=>void,
                                   failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        contentType_delete(params:api_remote_contenttype.DeleteParams, success:(result:api_remote_contenttype.DeleteResult)=>void,
                           failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        contentType_tree(params:api_remote_contenttype.GetTreeParams, success:(result:api_remote_contenttype.GetTreeResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteContentTypeServiceImpl = new RemoteContentTypeServiceImpl();
    RemoteContentTypeService = remoteContentTypeServiceImpl;
    remoteContentTypeServiceImpl.init();
}
