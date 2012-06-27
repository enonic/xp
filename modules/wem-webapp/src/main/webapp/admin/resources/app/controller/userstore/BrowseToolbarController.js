Ext.define('Admin.controller.userstore.BrowseToolbarController', {
    extend: 'Admin.controller.userstore.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [
    ],
    models: [
    ],
    views: [
        'Admin.view.userstore.DeleteUserstoreWindow'
    ],

    init: function () {

        this.control({
            'browseToolbar *[action=deleteUserstore]': {
                click: function () {
                    this.showDeleteUserstoreWindow();
                }
            },
            'browseToolbar *[action=viewUserstore]': {
                click: function () {
                    var userstore = this.getUserstoreGridPanel().getSelection()[0].data;
                    this.viewUserstore(userstore);
                }
            },
            'browseToolbar button[action=editUserstore]': {
                click: function (item, e, eOpts) {
                    var userstore = this.getUserstoreGridPanel().getSelection()[0].data;
                    this.createUserstoreTab(userstore);
                }
            },
            'browseToolbar *[action=newUserstore]': {
                'click': this.createUserstoreTab
            }
        });
    }

});
