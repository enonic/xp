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

            var remotingProvider:any = new Ext.direct.RemotingProvider(config);
            remotingProvider.getCallData = this.getCallData;
            remotingProvider.createEvent = this.createEvent;
            remotingProvider.runCallback = this.runCallback;

            this.ext = remotingProvider;
            this.ext.isProvider = true;

            this.initAPI(methods);
        }

        private initAPI(methods:string[]) {
            var namespace = this.ext.namespace;

            var methodName:string, length = methods.length;
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

        private getCallData(transaction:any):any {
            return {
                jsonrpc: '2.0',
                id: transaction.tid,
                method: transaction.method,
                params: transaction.data[0]
            };
        }

        private createEvent(response:any):any {
            var error:bool = response.error ? true : false;

            response.tid = response.id;
            response.type = error ? 'exception' : 'rpc';

            if (error) {
                response.message = response.error.message;
            }

            return Ext.create('direct.' + response.type, response);
        }

        private runCallback(transaction, event) {
            var success,
                successCallback,
                failureCallback,
                result,
                dataLength;

            if (transaction) {
                dataLength = transaction.data.length;
                successCallback = transaction.args[dataLength];
                failureCallback = transaction.args[dataLength + 1];

                result = Ext.isDefined(event.result) ? event.result : event.data;
                success = result && !Ext.isDefined(result.error);

                if (success && Ext.isFunction(successCallback)) {
                    successCallback(result, event);

                } else if (!success) {
                    var failureResult = {
                        error: result && result.error ? result.error : 'An unexpected error occurred:' + event.error.message
                    };

                    if (Ext.isFunction(failureCallback)) {
                        failureCallback(failureResult, event)
                    } else {
                        api_notify.showError(failureResult.error);
                    }
                }

            }
        }
    }
}
