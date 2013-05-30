Ext.define('Admin.lib.JsonRpcProvider', {

    alias: 'direct.jsonrpcprovider',
    extend: 'Ext.direct.RemotingProvider',

    initAPI: function () {
        var methods = this.methods;
        var namespace = this.namespace;

        var methodName;
        for (var i = 0; i < methods.length; i++) {
            methodName = methods[i];

            var def = {
                name: methodName,
                len: 1
            };

            var method = new Ext.direct.RemotingMethod(def);
            namespace[methodName] = this.createHandler(null, method);
        }
    },

    getCallData: function (transaction) {
        return {
            jsonrpc: '2.0',
            id: transaction.tid,
            method: transaction.method,
            params: transaction.data[0]
        };
    },

    createEvent: function (response) {
        var error = response.error ? true : false;

        response.tid = response.id;
        response.type = error ? 'exception' : 'rpc';

        if (error) {
            response.message = response.error.message;
        }

        return Ext.create('direct.' + response.type, response);
    }
});
