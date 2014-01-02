module api.remote {

    export class BaseRemoteService {
        private methods:string[];
        private namespace: string;

        constructor(namespace: string, methods:string[]) {
            this.methods = methods;
            this.namespace = namespace;
        }

        public init() {
            var url:string = api.util.getRestUri("jsonrpc");
            var jsonRpcProvider = new api.remote.JsonRpcProvider(url, this.methods, this.namespace);
            Ext.Direct.addProvider(jsonRpcProvider.ext);
        }
    }
}
