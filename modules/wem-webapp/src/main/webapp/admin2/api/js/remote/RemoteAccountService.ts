///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteAccountModel.ts' />

module api_remote {

    export var RemoteAccountService:RemoteAccountServiceInterface;

    export interface RemoteAccountServiceInterface {
        account_find (params:api_remote_account.FindParams, callback:(result:api_remote_account.FindResult)=>void):void;
        account_getGraph (params:api_remote_account.GetGraphParams, callback:(result:api_remote_account.GetGraphResult)=>void):void;
        account_changePassword (params:api_remote_account.ChangePasswordParams,
                                callback:(result:api_remote_account.ChangePasswordResult)=>void):void;
        account_verifyUniqueEmail (params:api_remote_account.VerifyUniqueEmailParams,
                                   callback:(result:api_remote_account.VerifyUniqueEmailResult)=>void):void;
        account_suggestUserName (params:api_remote_account.SuggestUserNameParams,
                                 callback:(result:api_remote_account.SuggestUserNameResult)=>void):void;
        account_createOrUpdate (params:api_remote_account.CreateOrUpdateParams,
                                callback:(result:api_remote_account.CreateOrUpdateResult)=>void):void;
        account_delete (params:api_remote_account.DeleteParams, callback:(result:api_remote_account.DeleteResult)=>void):void;
        account_get (params:api_remote_account.GetParams, callback:(result:api_remote_account.GetResult)=>void):void;
    }

    class RemoteAccountServiceImpl extends BaseRemoteService implements RemoteAccountServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "account_find", "account_getGraph", "account_changePassword", "account_verifyUniqueEmail", "account_suggestUserName",
                "account_createOrUpdate", "account_delete", "account_get"
            ];
            super('api_remote.RemoteAccountService', methods);
        }

        account_find(params:api_remote_account.FindParams, callback:(result:api_remote_account.FindResult)=>void):void {
            console.log(params, callback);
        }

        account_getGraph(params:api_remote_account.GetGraphParams, callback:(result:api_remote_account.GetGraphResult)=>void):void {
            console.log(params, callback);
        }

        account_changePassword(params:api_remote_account.ChangePasswordParams,
                               callback:(result:api_remote_account.ChangePasswordResult)=>void):void {
            console.log(params, callback);
        }

        account_verifyUniqueEmail(params:api_remote_account.VerifyUniqueEmailParams,
                                  callback:(result:api_remote_account.VerifyUniqueEmailResult)=>void):void {
            console.log(params, callback);
        }

        account_suggestUserName(params:api_remote_account.SuggestUserNameParams,
                                callback:(result:api_remote_account.SuggestUserNameResult)=>void):void {
            console.log(params, callback);
        }

        account_createOrUpdate(params:api_remote_account.CreateOrUpdateParams,
                               callback:(result:api_remote_account.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        account_delete(params:api_remote_account.DeleteParams, callback:(result:api_remote_account.DeleteResult)=>void):void {
            console.log(params, callback);
        }

        account_get(params:api_remote_account.GetParams, callback:(result:api_remote_account.GetResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteAccountServiceImpl = new RemoteAccountServiceImpl();
    RemoteAccountService = remoteAccountServiceImpl;
    remoteAccountServiceImpl.init();
}
