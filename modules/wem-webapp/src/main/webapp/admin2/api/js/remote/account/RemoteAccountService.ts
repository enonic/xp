///<reference path='../BaseRemoteService.ts' />
///<reference path='RemoteAccountModel.ts' />

module api_remote_account {

    export var RemoteAccountService:RemoteAccountServiceInterface;

    export interface RemoteAccountServiceInterface {
        account_find (params:api_remote_account.FindParams, success:(result:api_remote_account.FindResult)=>void,
                      failure?:(result:api_remote.FailureResult)=>void):void;
        account_getGraph (params:api_remote_account.GetGraphParams, success:(result:api_remote_account.GetGraphResult)=>void,
                          failure?:(result:api_remote.FailureResult)=>void):void;
        account_changePassword (params:api_remote_account.ChangePasswordParams,
                                success:(result:api_remote_account.ChangePasswordResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void;
        account_verifyUniqueEmail (params:api_remote_account.VerifyUniqueEmailParams,
                                   success:(result:api_remote_account.VerifyUniqueEmailResult)=>void,
                                   failure?:(result:api_remote.FailureResult)=>void):void;
        account_suggestUserName (params:api_remote_account.SuggestUserNameParams,
                                 success:(result:api_remote_account.SuggestUserNameResult)=>void,
                                 failure?:(result:api_remote.FailureResult)=>void):void;
        account_createOrUpdate (params:api_remote_account.CreateOrUpdateParams,
                                success:(result:api_remote_account.CreateOrUpdateResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void;
        account_delete (params:api_remote_account.DeleteParams, success:(result:api_remote_account.DeleteResult)=>void,
                        failure?:(result:api_remote.FailureResult)=>void):void;
        account_get (params:api_remote_account.GetParams, success:(result:api_remote_account.GetResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteAccountServiceImpl extends api_remote.BaseRemoteService implements RemoteAccountServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "account_find", "account_getGraph", "account_changePassword", "account_verifyUniqueEmail", "account_suggestUserName",
                "account_createOrUpdate", "account_delete", "account_get"
            ];
            super('api_remote_account.RemoteAccountService', methods);
        }

        account_find(params:api_remote_account.FindParams, success:(result:api_remote_account.FindResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_getGraph(params:api_remote_account.GetGraphParams, success:(result:api_remote_account.GetGraphResult)=>void,
                         failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_changePassword(params:api_remote_account.ChangePasswordParams,
                               success:(result:api_remote_account.ChangePasswordResult)=>void,
                               failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_verifyUniqueEmail(params:api_remote_account.VerifyUniqueEmailParams,
                                  success:(result:api_remote_account.VerifyUniqueEmailResult)=>void,
                                  failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_suggestUserName(params:api_remote_account.SuggestUserNameParams,
                                success:(result:api_remote_account.SuggestUserNameResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_createOrUpdate(params:api_remote_account.CreateOrUpdateParams,
                               success:(result:api_remote_account.CreateOrUpdateResult)=>void,
                               failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_delete(params:api_remote_account.DeleteParams, success:(result:api_remote_account.DeleteResult)=>void,
                       failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        account_get(params:api_remote_account.GetParams, success:(result:api_remote_account.GetResult)=>void,
                    failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteAccountServiceImpl = new RemoteAccountServiceImpl();
    RemoteAccountService = remoteAccountServiceImpl;
    remoteAccountServiceImpl.init();
}
