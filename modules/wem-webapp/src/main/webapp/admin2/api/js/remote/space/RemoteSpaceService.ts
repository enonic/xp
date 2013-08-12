module api_remote_space {

    export var RemoteSpaceService:RemoteSpaceServiceInterface;

    export interface RemoteSpaceServiceInterface {
        space_list (params:api_remote_space.ListParams, success:(result:api_remote_space.ListResult)=>void,
                    failure?:(result:api_remote.FailureResult)=>void):void;
        space_get (params:api_remote_space.GetParams, success:(result:api_remote_space.GetResult)=>void,
                   failure?:(result:api_remote.FailureResult)=>void):void;
        space_delete (params:api_remote_space.DeleteParams, success:(result:api_remote_space.DeleteResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
        space_createOrUpdate (params:api_remote_space.CreateOrUpdateParams, success:(result:api_remote_space.CreateOrUpdateResult)=>void,
                              failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteSpaceServiceImpl extends api_remote.BaseRemoteService implements RemoteSpaceServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "space_list", "space_get", "space_delete", "space_createOrUpdate"
            ];
            super('api_remote_space.RemoteSpaceService', methods);
        }

        space_list(params:api_remote_space.ListParams, success:(result:api_remote_space.ListResult)=>void,
                   failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        space_get(params:api_remote_space.GetParams, success:(result:api_remote_space.GetResult)=>void,
                  failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        space_delete(params:api_remote_space.DeleteParams, success:(result:api_remote_space.DeleteResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        space_createOrUpdate(params:api_remote_space.CreateOrUpdateParams, success:(result:api_remote_space.CreateOrUpdateResult)=>void,
                             failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteSpaceServiceImpl = new RemoteSpaceServiceImpl();
    RemoteSpaceService = remoteSpaceServiceImpl;
    remoteSpaceServiceImpl.init();
}
