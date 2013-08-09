///<reference path='../BaseRemoteService.ts' />
///<reference path='RemoteContentModel.ts' />

module api_remote_content {

    export var RemoteContentService:RemoteContentServiceInterface;

    export interface RemoteContentServiceInterface {
        content_createOrUpdate (params:api_remote_content.CreateOrUpdateParams,
                                success:(result:api_remote_content.CreateOrUpdateResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void;
        content_list (params:api_remote_content.ListParams, success:(result:api_remote_content.ListResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
        content_tree (params:api_remote_content.GetTreeParams, success:(result:api_remote_content.GetTreeResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
        content_get (params:api_remote_content.GetParams, success:(result:api_remote_content.GetResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void;
        content_delete (params:api_remote_content.DeleteParams, success:(result:api_remote_content.DeleteResult)=>void,
                        failure?:(result:api_remote.FailureResult)=>void):void;
        content_find (params:api_remote_content.FindParams, success:(result:api_remote_content.FindResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
        content_validate (params:api_remote_content.ValidateParams, success:(result:api_remote_content.ValidateResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
        binary_create (params, success, failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteContentServiceImpl extends api_remote.BaseRemoteService implements RemoteContentServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "content_createOrUpdate", "content_list", "content_tree", "content_get",
                "content_delete", "content_validate", "content_find", "binary_create"
            ];
            super('api_remote_content.RemoteContentService', methods);
        }

        content_createOrUpdate(params:api_remote_content.CreateOrUpdateParams,
                               success:(result:api_remote_content.CreateOrUpdateResult)=>void,
                               failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_list(params:api_remote_content.ListParams, success:(result:api_remote_content.ListResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_tree(params:api_remote_content.GetTreeParams, success:(result:api_remote_content.GetTreeResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_get(params:api_remote_content.GetParams, success:(result:api_remote_content.GetResult)=>void,
                    failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_delete(params:api_remote_content.DeleteParams, success:(result:api_remote_content.DeleteResult)=>void,
                       failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_find(params:api_remote_content.FindParams, success:(result:api_remote_content.FindResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        content_validate(params:api_remote_content.ValidateParams, success:(result:api_remote_content.ValidateResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        binary_create(params, success, failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteContentServiceImpl = new RemoteContentServiceImpl();
    RemoteContentService = remoteContentServiceImpl;
    remoteContentServiceImpl.init();
}
