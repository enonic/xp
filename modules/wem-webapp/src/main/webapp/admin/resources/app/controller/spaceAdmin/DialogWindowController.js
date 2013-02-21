Ext.define('Admin.controller.spaceAdmin.DialogWindowController', {
    extend: 'Admin.controller.spaceAdmin.SpaceController',

    stores: [],
    models: [],
    views: [
        'Admin.view.spaceAdmin.DeleteSpaceWindow'
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
            spaceNames = [],
            me = this;
        var onDelete = function (success, details) {
            win.close();
            if (success && details.deleted) {

                Admin.MessageBus.showFeedback({
                    title: 'Space deleted',
                    message: spaceNames.length > 1? spaceNames.join(', ') + ' were deleted' : spaceNames[0] + ' was deleted' ,
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
        }

        if (win.modelData.selection) {
            Ext.each(win.modelData.selection, function (space) {
                spaceNames.push(space.data.name);
            });
        } else {
            spaceNames.push(win.modelData.name);
        }
        this.remoteDeleteSpace(spaceNames, onDelete);
    }

});