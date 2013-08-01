///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteUserStoreModel.ts' />

module api_remote {

    export var RemoteUserStoreService:RemoteUserStoreServiceInterface;

    export interface RemoteUserStoreServiceInterface {
        userstore_getAll (params:api_remote_userstore.GetAllParams, callback:(result:api_remote_userstore.GetAllResult)=>void):void;
        userstore_get (params:api_remote_userstore.GetParams, callback:(result:api_remote_userstore.GetResult)=>void):void;
        userstore_getConnectors (params:api_remote_userstore.GetConnectorsParams,
                                 callback:(result:api_remote_userstore.GetConnectorsResult)=>void):void;
        userstore_createOrUpdate (params:api_remote_userstore.CreateOrUpdateParams,
                                  callback:(result:api_remote_userstore.CreateOrUpdateResult)=>void):void;
        userstore_delete (params:api_remote_userstore.DeleteParams, callback:(result:api_remote_userstore.DeleteResult)=>void):void;
    }

    class RemoteUserStoreServiceImpl extends BaseRemoteService implements RemoteUserStoreServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "userstore_getAll", "userstore_get", "userstore_getConnectors",
                "userstore_createOrUpdate", "userstore_delete"
            ];
            super('api_remote.RemoteUserStoreService', methods);
        }

        userstore_getAll(params:api_remote_userstore.GetAllParams, callback:(result:api_remote_userstore.GetAllResult)=>void):void {
            console.log(params, callback);
        }

        userstore_get(params:api_remote_userstore.GetParams, callback:(result:api_remote_userstore.GetResult)=>void):void {
            console.log(params, callback);
        }

        userstore_getConnectors(params:api_remote_userstore.GetConnectorsParams,
                                callback:(result:api_remote_userstore.GetConnectorsResult)=>void):void {
            console.log(params, callback);
        }

        userstore_createOrUpdate(params:api_remote_userstore.CreateOrUpdateParams,
                                 callback:(result:api_remote_userstore.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        userstore_delete(params:api_remote_userstore.DeleteParams, callback:(result:api_remote_userstore.DeleteResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteUserStoreServiceImpl = new RemoteUserStoreServiceImpl();
    RemoteUserStoreService = remoteUserStoreServiceImpl;
    remoteUserStoreServiceImpl.init();
}
