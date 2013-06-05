Ext.define('Admin.controller.DialogWindowController', {
    extend: 'Admin.controller.SpaceController',

    stores: [],
    models: [],
    views: [],


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

                api_notify.showFeedback(Ext.isArray(space) && space.length > 1 ? space.length + ' spaces were deleted'
                    : '1 space was deleted');

            } else {
                var message:string = details.reason;
                api_notify.showFeedback(message);

            }
            me.getSpaceTreeGridPanel().refresh();
        };

        this.remoteDeleteSpace(space, onDelete);
    }

});