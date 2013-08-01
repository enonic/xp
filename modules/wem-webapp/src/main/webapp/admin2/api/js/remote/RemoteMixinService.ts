///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteMixinModel.ts' />

module api_remote {

    export var RemoteMixinService:RemoteMixinServiceInterface;

    export interface RemoteMixinServiceInterface {
        mixin_get (params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void):void;
        mixin_createOrUpdate (params:api_remote_mixin.CreateOrUpdateParams,
                              callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void):void;
        mixin_delete (params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void):void;
    }

    class RemoteMixinServiceImpl extends BaseRemoteService implements RemoteMixinServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "mixin_get", "mixin_createOrUpdate", "mixin_delete"
            ];
            super('api_remote.RemoteMixinService', methods);
        }

        mixin_get(params:api_remote_mixin.GetParams, callback:(result:api_remote_mixin.GetResult)=>void):void {
            console.log(params, callback);
        }

        mixin_createOrUpdate(params:api_remote_mixin.CreateOrUpdateParams,
                             callback:(result:api_remote_mixin.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        mixin_delete(params:api_remote_mixin.DeleteParams, callback:(result:api_remote_mixin.DeleteResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteMixinServiceImpl = new RemoteMixinServiceImpl();
    RemoteMixinService = remoteMixinServiceImpl;
    remoteMixinServiceImpl.init();
}
