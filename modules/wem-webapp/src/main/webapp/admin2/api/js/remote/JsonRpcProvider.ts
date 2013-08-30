module api_remote {

    export class JsonRpcProvider {
        ext:Ext_direct_RemotingProvider;

        constructor(url:string, methods:string[], namespace:string) {
            var config = {
                url: url,
                type: 'jsonrpc',
                namespace: namespace,
                methods: methods,
                enableBuffer: 20,
                alias: 'direct.jsonrpcprovider',
                runCallback: this.runCallback,
                getCallData: this.getCallData,
                isProvider: true
            };

            this.ext = new Ext.direct.RemotingProvider(config);
            this.ext.createEvent = this.createEvent;

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

        private createEvent(response:any):Ext_direct_Event {
            var error:boolean = response.error ? true : false;

            response.tid = response.id;
            response.type = error ? 'exception' : 'rpc';

            if (error) {
                response.message = response.error.message;
            }

            return <Ext_direct_Event> Ext.create('direct.' + response.type, response);
        }

        private runCallback(transaction, event) {
            var success:boolean,
                successCallback:(successResult:any, event:any) => void,
                failureCallback:(failureResult:FailureResult, event:any) => void,
                result:any,
                dataLength:number;

            if (!transaction) {
                return;
            }

            dataLength = transaction.data.length;
            successCallback = transaction.args[dataLength];
            failureCallback = transaction.args[dataLength + 1];

            result = Ext.isDefined(event.result) ? event.result : event.data;
            success = result && !Ext.isDefined(result.error);

            if (success && successCallback) {
                successCallback(result, event);
            } else if (!success) {
                var errorMessage = result && result.error ? result.error : 'An unexpected error occurred';
                if (event.error != null) {
                    errorMessage += ": " + event.error.message;
                }
                var failureResult:FailureResult = {
                    error: errorMessage
                };

                if (failureCallback) {
                    failureCallback(failureResult, event)
                } else {
                    api_notify.showError(failureResult.error);
                }
            }
        }

    }
}
