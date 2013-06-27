Ext.define('Admin.controller.DetailToolbarController', {
    extend: 'Admin.controller.Controller',

    stores: [
    ],

    models: [
    ],
    /*    views: [
     'Admin.view.contentManager.DetailToolbar'
     ],*/

    init: function () {

        this.control({
            '#contentDetailToolbar *[action=newContent]': {
                click: function (el, e) {
                    app_browse.ContentBrowseActions.NEW_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=editContent]': {
                click: function (el, e) {
                    app_browse.ContentBrowseActions.EDIT_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=deleteContent]': {
                click: function (el, e) {
                    app_browse.ContentBrowseActions.DELETE_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=duplicateContent]': {
                click: function (el, e) {
                    app_browse.ContentBrowseActions.DUPLICATE_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=moveContent]': {
                click: function (el, e) {
                    app_browse.ContentBrowseActions.MOVE_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=relations]': {
                click: function (el, e) {

                }
            },
            '#contentDetailToolbar *[action=closeContent]': {
                click: function (el, e) {
                    this.getCmsTabPanel().getActiveTab().close();
                }
            },
            '#contentDetailToolbar *[action=toggleLive]': {
                change: function (slider, state) {
                    slider.up().down('#deviceCycle').setDisabled(!state);
                }
            },
            '#contentDetailToolbar #deviceCycle': {
                change: function (cycle, item) {
                    this.application.fireEvent('toggleDeviceContext', item.device);
                }
            }
        });
    }

});
