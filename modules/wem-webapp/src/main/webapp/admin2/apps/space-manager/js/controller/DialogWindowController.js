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

                Admin.MessageBus.showFeedback({
                    title: 'Space deleted',
                    message: Ext.isArray(space) && space.length > 1 ? space.length + ' spaces were deleted' : '1 space was deleted',
                    opts: {}
                });

            } else {
                var message = details.reason;
                Admin.MessageBus.showFeedback({
                    title: 'Space could not be deleted',
                    message: message,
                    opts: {}
                });

            }
            me.getSpaceTreeGridPanel().refresh();
        };

        this.remoteDeleteSpace(space, onDelete);
    }

});