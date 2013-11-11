module api_remote_contenttype {

    export var RemoteContentTypeService:RemoteContentTypeServiceInterface;

    export interface RemoteContentTypeServiceInterface {
        contentType_get (params:api_remote_contenttype.GetParams, success:(result:api_remote_contenttype.GetResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_list (params:api_remote_contenttype.ListParams, success:(result:api_remote_contenttype.ListResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
        contentType_tree (params:api_remote_contenttype.GetTreeParams, success:(result:api_remote_contenttype.GetTreeResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteContentTypeServiceImpl extends api_remote.BaseRemoteService implements RemoteContentTypeServiceInterface {

        constructor() {
            var methods:string[] = [
                "contentType_get", "contentType_list",  "contentType_tree"
            ];
            super('api_remote_contenttype.RemoteContentTypeService', methods);
        }

        contentType_get(params:api_remote_contenttype.GetParams, success:(result:api_remote_contenttype.GetResult)=>void,
                        failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        contentType_list(params:api_remote_contenttype.ListParams, success:(result:api_remote_contenttype.ListResult)=>void,
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
