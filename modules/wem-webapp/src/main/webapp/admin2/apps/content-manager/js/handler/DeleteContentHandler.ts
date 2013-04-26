module admin.app.handler {
    export class DeleteContentHandler {
        doDelete(spaces, callback:(obj, success, result) => void) {
            var me = this;
            var spaceNames = Ext.Array.map([].concat(spaces), function (item) {
                return item.get('name');
            });

            /*
            ToDo: implement remote content_delete
            Admin.lib.RemoteService.space_delete({
                "spaceName": spaceNames
            }, function (r) {
                if (r) {
                    callback(me, r.success, r);
                } else {
                    Ext.Msg.alert("Error", r ? r.error : "Unable to delete space.");
                }
            });
            */
        }
    }
}