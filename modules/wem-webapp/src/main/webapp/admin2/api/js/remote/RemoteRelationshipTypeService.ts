///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteRelationshipTypeModel.ts' />

module api_remote {

    export var RemoteRelationshipTypeService:RemoteRelationshipTypeServiceInterface;

    export interface RemoteRelationshipTypeServiceInterface {
        relationshipType_delete (params:api_remote_relationshiptype.DeleteParams,
                                 callback:(result:api_remote_relationshiptype.DeleteResult)=>void):void;
        relationshipType_get (params:api_remote_relationshiptype.GetParams,
                              callback:(result:api_remote_relationshiptype.GetResult)=>void):void;
        relationshipType_createOrUpdate (params:api_remote_relationshiptype.CreateOrUpdateParams,
                                         callback:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void):void;
    }

    class RemoteRelationshipTypeServiceImpl extends BaseRemoteService implements RemoteRelationshipTypeServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "relationshipType_get", "relationshipType_createOrUpdate", "relationshipType_delete"
            ];
            super('api_remote.RemoteRelationshipTypeService', methods);
        }

        relationshipType_get(params:api_remote_relationshiptype.GetParams,
                             callback:(result:api_remote_relationshiptype.GetResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_createOrUpdate(params:api_remote_relationshiptype.CreateOrUpdateParams,
                                        callback:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void):void {
            console.log(params, callback);
        }

        relationshipType_delete(params:api_remote_relationshiptype.DeleteParams,
                                callback:(result:api_remote_relationshiptype.DeleteResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteRelationshipTypeServiceImpl = new RemoteRelationshipTypeServiceImpl();
    RemoteRelationshipTypeService = remoteRelationshipTypeServiceImpl;
    remoteRelationshipTypeServiceImpl.init();
}
