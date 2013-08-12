module api_remote_mixin {

    export var RemoteMixinService:RemoteMixinServiceInterface;

    export interface RemoteMixinServiceInterface {
        mixin_get (params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void,
                   failure?:(result:api_remote.FailureResult)=>void):void;
        mixin_createOrUpdate (params:api_remote_mixin.CreateOrUpdateParams, callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void,
                              failure?:(result:api_remote.FailureResult)=>void):void;
        mixin_delete (params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteMixinServiceImpl extends api_remote.BaseRemoteService implements RemoteMixinServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "mixin_get", "mixin_createOrUpdate", "mixin_delete"
            ];
            super('api_remote_mixin.RemoteMixinService', methods);
        }

        mixin_get(params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void,
                  failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, callback, failure);
        }

        mixin_createOrUpdate(params:api_remote_mixin.CreateOrUpdateParams, callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void,
                             failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, callback, failure);
        }

        mixin_delete(params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, callback, failure);
        }
    }

    var remoteMixinServiceImpl = new RemoteMixinServiceImpl();
    RemoteMixinService = remoteMixinServiceImpl;
    remoteMixinServiceImpl.init();
}
