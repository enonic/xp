///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteSpaceModel.ts' />

module api_remote {

    export var RemoteSpaceService:RemoteSpaceServiceInterface;

    export interface RemoteSpaceServiceInterface {
        space_list (params:api_remote_space.ListParams, callback:(result:api_remote_space.ListResult)=>void):void;
        space_get (params:api_remote_space.GetParams, callback:(result:api_remote_space.GetResult)=>void):void;
        space_delete (params:api_remote_space.DeleteParams, callback:(result:api_remote_space.DeleteResult)=>void):void;
        space_createOrUpdate (params:api_remote_space.CreateOrUpdateParams,
                              callback:(result:api_remote_space.CreateOrUpdateResult)=>void):void;
    }

    class RemoteSpaceServiceImpl extends BaseRemoteService implements RemoteSpaceServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "space_list", "space_get", "space_delete", "space_createOrUpdate"
            ];
            super('api_remote.RemoteSpaceService', methods);
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
    }

    var remoteSpaceServiceImpl = new RemoteSpaceServiceImpl();
    RemoteSpaceService = remoteSpaceServiceImpl;
    remoteSpaceServiceImpl.init();
}
