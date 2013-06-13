module admin.app.handler {

    export class DeleteContentHandler {

        doDelete(contentModels, callback:(thisArg, success, result) => void) {
            var contentPaths = Ext.Array.map([].concat(contentModels), function (item) {
                return item.get('path');
            });

            api_remote.RemoteService.content_delete({'contentPaths': contentPaths }, (response) => {
                if (response) {
                    callback.call(this, response.success, response.failures);
                } else {
                    Ext.Msg.alert('Error', response ? response.error : 'Internal error occured.');
                }
            });

        }

    }
}
