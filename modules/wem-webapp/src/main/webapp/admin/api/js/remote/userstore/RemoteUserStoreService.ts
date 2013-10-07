module api_remote_userstore {

    export var RemoteUserStoreService:RemoteUserStoreServiceInterface;

    export interface RemoteUserStoreServiceInterface {
        userstore_getAll (params:api_remote_userstore.GetAllParams, success:(result:api_remote_userstore.GetAllResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
        userstore_get (params:api_remote_userstore.GetParams, success:(result:api_remote_userstore.GetResult)=>void,
                       failure?:(result:api_remote.FailureResult)=>void):void;
        userstore_getConnectors (params:api_remote_userstore.GetConnectorsParams,
                                 success:(result:api_remote_userstore.GetConnectorsResult)=>void,
                                 failure?:(result:api_remote.FailureResult)=>void):void;
        userstore_createOrUpdate (params:api_remote_userstore.CreateOrUpdateParams,
                                  success:(result:api_remote_userstore.CreateOrUpdateResult)=>void,
                                  failure?:(result:api_remote.FailureResult)=>void):void;
        userstore_delete (params:api_remote_userstore.DeleteParams, success:(result:api_remote_userstore.DeleteResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteUserStoreServiceImpl extends api_remote.BaseRemoteService implements RemoteUserStoreServiceInterface {

        constructor() {
            var methods:string[] = [
                "userstore_getAll", "userstore_get", "userstore_getConnectors",
                "userstore_createOrUpdate", "userstore_delete"
            ];
            super('api_remote_userstore.RemoteUserStoreService', methods);
        }

        userstore_getAll(params:api_remote_userstore.GetAllParams, success:(result:api_remote_userstore.GetAllResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        userstore_get(params:api_remote_userstore.GetParams, success:(result:api_remote_userstore.GetResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        userstore_getConnectors(params:api_remote_userstore.GetConnectorsParams,
                                success:(result:api_remote_userstore.GetConnectorsResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        userstore_createOrUpdate(params:api_remote_userstore.CreateOrUpdateParams,
                                 success:(result:api_remote_userstore.CreateOrUpdateResult)=>void,
                                 failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        userstore_delete(params:api_remote_userstore.DeleteParams, success:(result:api_remote_userstore.DeleteResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteUserStoreServiceImpl = new RemoteUserStoreServiceImpl();
    RemoteUserStoreService = remoteUserStoreServiceImpl;
    remoteUserStoreServiceImpl.init();
}
