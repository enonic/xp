module admin.app.handler {
    export class DeleteContentHandler {
        doDelete(contentModels, callback:(obj, success, result) => void) {
            var me = this;
            var contentKeys = Ext.Array.map([].concat(contentModels), function (item) {
                return item.get('key');
            });
        }
    }
}