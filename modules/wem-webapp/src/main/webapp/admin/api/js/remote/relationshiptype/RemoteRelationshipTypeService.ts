module api_remote_relationshiptype {

    export var RemoteRelationshipTypeService:RemoteRelationshipTypeServiceInterface;

    export interface RemoteRelationshipTypeServiceInterface {
        relationshipType_delete (params:api_remote_relationshiptype.DeleteParams,
                                 success:(result:api_remote_relationshiptype.DeleteResult)=>void,
                                 failure?:(result:api_remote.FailureResult)=>void):void;
        relationshipType_get (params:api_remote_relationshiptype.GetParams, success:(result:api_remote_relationshiptype.GetResult)=>void,
                              failure?:(result:api_remote.FailureResult)=>void):void;
        relationshipType_createOrUpdate (params:api_remote_relationshiptype.CreateOrUpdateParams,
                                         success:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void,
                                         failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteRelationshipTypeServiceImpl extends api_remote.BaseRemoteService implements RemoteRelationshipTypeServiceInterface {

        constructor() {
            var methods:string[] = [
                "relationshipType_get", "relationshipType_createOrUpdate", "relationshipType_delete"
            ];
            super('api_remote_relationshiptype.RemoteRelationshipTypeService', methods);
        }

        relationshipType_get(params:api_remote_relationshiptype.GetParams, success:(result:api_remote_relationshiptype.GetResult)=>void,
                             failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        relationshipType_createOrUpdate(params:api_remote_relationshiptype.CreateOrUpdateParams,
                                        success:(result:api_remote_relationshiptype.CreateOrUpdateResult)=>void,
                                        failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        relationshipType_delete(params:api_remote_relationshiptype.DeleteParams,
                                success:(result:api_remote_relationshiptype.DeleteResult)=>void,
                                failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteRelationshipTypeServiceImpl = new RemoteRelationshipTypeServiceImpl();
    RemoteRelationshipTypeService = remoteRelationshipTypeServiceImpl;
    remoteRelationshipTypeServiceImpl.init();
}
