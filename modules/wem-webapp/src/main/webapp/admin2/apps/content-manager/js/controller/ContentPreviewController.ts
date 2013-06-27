Ext.define('Admin.controller.ContentPreviewController', {
    extend: 'Admin.controller.ContentController',

    /*      Controller for handling Content Preview UI events       */

    stores: [],
    models: [],
    views: [],

    init: function () {
        this.control({
            '#contentDetailToolbar *[action=duplicateContent]': {
                click: (el, e) => {
                    app_browse.ContentBrowseActions.DUPLICATE_CONTENT.execute();
                }
            },
            '#contentDetailToolbar *[action=deleteContent]': {
                click: (el, e) => {
                    app_browse.ContentBrowseActions.DELETE_CONTENT.execute();
                }
            },
            '#contentDetailToolbar toggleslide': {
                change: this.toggleLiveDetail
            }
        });

        this.application.on({
            toggleDeviceContext: function (device) {
                var previewPanel = this.getContentPreviewPanel().down('#livePreview');
                previewPanel.resizeIframe(this.getDimensionsForDevice(device));
            },
            scope: this
        });
    },


    toggleLiveDetail: function (el, e) {
        this.getContentPreviewPanel().toggleLive();
    },


    /*      Getters     */

    getContentPreviewTab: function () {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentPreviewPanel: function () {
        return this.getContentPreviewTab();
    }

});

