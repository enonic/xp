module api_remote {

    export class JsonRpcProvider {
        ext:any; //Ext_direct_RemotingProvider;

        constructor(url:string, methods:string[], namespace:string) {
            var config = {
                url: url,
                type: 'jsonrpc',
                namespace: namespace,
                methods: methods,
                enableBuffer: 20,
                alias: 'direct.jsonrpcprovider'
            };

            var remotingProvider = new Ext.direct.RemotingProvider(config);
            remotingProvider.getCallData = this.getCallData;
            remotingProvider.createEvent = this.createEvent;

            this.ext = remotingProvider;
            this.ext.isProvider = true;

            this.initAPI(methods);
        }

        private initAPI(methods:string[]) {
            var namespace = this.ext.namespace;

            var methodName: string, length = methods.length;
            for (var i = 0; i < length; i++) {
                methodName = methods[i];

                var def = {
                    name: methodName,
                    len: 1
                };

                var method = new Ext.direct.RemotingMethod(def);
                namespace[methodName] = this.ext.createHandler(null, method);
            }
        }

        private getCallData(transaction:any): any {
            return {
                jsonrpc: '2.0',
                id: transaction.tid,
                method: transaction.method,
                params: transaction.data[0]
            };
        }

        private createEvent(response:any): any {
            var error:bool = response.error ? true : false;

            response.tid = response.id;
            response.type = error ? 'exception' : 'rpc';

            if (error) {
                response.message = response.error.message;
            }

            return Ext.create('direct.' + response.type, response);
        }
    }

}
