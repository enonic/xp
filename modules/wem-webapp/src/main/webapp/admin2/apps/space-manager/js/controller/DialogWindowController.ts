Ext.define('Admin.controller.DialogWindowController', {
    extend: 'Admin.controller.SpaceController',

    stores: [],
    models: [],
    views: [
        'Admin.view.DeleteSpaceWindow'
    ],


    init: function () {

        this.control({
            'deleteSpaceWindow *[action=deleteSpace]': {
                click: this.deleteSpace
            }
        });
    },

    deleteSpace: function () {
        var win = this.getDeleteSpaceWindow(),
            space = win.data,
            me = this;

        var onDelete = function (success, details) {
            win.close();
            if (success && details.deleted) {

                admin.api.message.showFeedback(Ext.isArray(space) && space.length > 1 ? space.length + ' spaces were deleted'
                    : '1 space was deleted');

            } else {
                var message:String = details.reason;
                admin.api.message.showFeedback(message);

            }
            me.getSpaceTreeGridPanel().refresh();
        };

        this.remoteDeleteSpace(space, onDelete);
    }

});