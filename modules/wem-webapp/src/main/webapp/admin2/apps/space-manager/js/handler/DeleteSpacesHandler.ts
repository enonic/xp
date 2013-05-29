module admin.app.handler {

    export class DeleteSpacesHandler {

        doDelete(spaceModels, callback:(thisArg, success, result) => void) {
            var spaceNames = Ext.Array.map([].concat(spaceModels), (item) => {
                if (!item) {
                    console.error('No spaces selected');
                }
                return item.get('name');
            });

            Admin.lib.RemoteService.space_delete({ 'spaceName': spaceNames}, function (response) {
                if (response) {
                    callback.call(this, response.success, response);
                } else {
                    console.error('Error', response ? response.error : 'Unable to delete space.');
                }
            });
        }
    }
}