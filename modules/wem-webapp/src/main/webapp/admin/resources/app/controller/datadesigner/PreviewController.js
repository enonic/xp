Ext.define('Admin.controller.datadesigner.PreviewController', {
    extend: 'Admin.controller.datadesigner.Controller',

    stores: [],

    models: [],

    views: [],

    init: function () {
        this.control(
            {
                'contentTypeDetailPanel *[action=closePreview]': {
                    click: this.closePreview
                },
                'contentTypeDetailPanel *[action=deleteContentType]': {
                    click: function (btn, evt) {
                        var contentType = this.getContentTypeData();
                        this.showDeleteContentTypeWindow(contentType);
                    }
                },
                'contentTypeDetailPanel *[action=editContentType]': {
                    click: function () {
                        var contentType = this.getContentTypeData();
                        this.closePreview();
                        this.createEditContentPanel(contentType);
                    }
                }
            }
        );
    },

    closePreview: function () {
        this.getCurrentTab().close();
    },

    getContentTypeData: function () {
        return this.getCurrentTab().getData();
    }

});
