module api_remote_schema {

    export var RemoteSchemaService:RemoteSchemaServiceInterface;

    export interface RemoteSchemaServiceInterface {
        schema_tree (params:api_remote_schema.GetTreeParams, success:(result:api_remote_schema.GetTreeResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void;
        schema_list (params:api_remote_schema.ListParams, success:(result:api_remote_schema.ListResult)=>void,
                     failure?:(result:api_remote.FailureResult)=>void):void;
    }

    class RemoteSchemaServiceImpl extends api_remote.BaseRemoteService implements RemoteSchemaServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "schema_list", "schema_tree"
            ];
            super('api_remote_schema.RemoteSchemaService', methods);
        }

        schema_tree(params:api_remote_schema.GetTreeParams, success:(result:api_remote_schema.GetTreeResult)=>void,
                    failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }

        schema_list(params:api_remote_schema.ListParams, success:(result:api_remote_schema.ListResult)=>void,
                    failure?:(result:api_remote.FailureResult)=>void):void {
            console.log(params, success, failure);
        }
    }

    var remoteSchemaServiceImpl = new RemoteSchemaServiceImpl();
    RemoteSchemaService = remoteSchemaServiceImpl;
    remoteSchemaServiceImpl.init();
}
