module api_remote {

    export class BaseRemoteService {
        private provider:any; //Ext_direct_RemotingProvider;
        private methods:string[];
        private namespace: string;

        constructor(namespace: string, methods:string[]) {
            this.methods = methods;
            this.namespace = namespace;
        }

        public init() {
            var url:string = api_util.getAbsoluteUri("admin/rest/jsonrpc");
            var jsonRpcProvider = new api_remote.JsonRpcProvider(url, this.methods, this.namespace);
            this.provider = Ext.Direct.addProvider(jsonRpcProvider.ext);
        }
    }
}
