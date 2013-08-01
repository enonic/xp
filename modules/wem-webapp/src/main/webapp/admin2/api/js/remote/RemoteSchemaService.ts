///<reference path='BaseRemoteService.ts' />
///<reference path='Item.ts' />
///<reference path='RemoteSchemaModel.ts' />

module api_remote {

    export var RemoteSchemaService:RemoteSchemaServiceInterface;

    export interface RemoteSchemaServiceInterface {
        schema_tree (params:api_remote_schema.GetTreeParams, callback:(result:api_remote_schema.GetTreeResult)=>void):void;
        schema_list (params:api_remote_schema.ListParams, callback:(result:api_remote_schema.ListResult)=>void):void;
    }

    class RemoteSchemaServiceImpl extends BaseRemoteService implements RemoteSchemaServiceInterface {
        private provider:any; //Ext_direct_RemotingProvider;

        constructor() {
            var methods:string[] = [
                "schema_list", "schema_tree"
            ];
            super('api_remote.RemoteSchemaService', methods);
        }

        schema_tree(params:api_remote_schema.GetTreeParams, callback:(result:api_remote_schema.GetTreeResult)=>void):void {
            console.log(params, callback);
        }

        schema_list(params:api_remote_schema.ListParams, callback:(result:api_remote_schema.ListResult)=>void):void {
            console.log(params, callback);
        }
    }

    var remoteSchemaServiceImpl = new RemoteSchemaServiceImpl();
    RemoteSchemaService = remoteSchemaServiceImpl;
    remoteSchemaServiceImpl.init();
}
