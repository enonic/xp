Ext.define('Admin.controller.DialogWindowController', {
    extend: 'Admin.controller.ContentController',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    /*    views: [
     'Admin.view.contentManager.DeleteContentWindow'
     ],*/


    init: function () {

        this.control({
            'newContentWindow': {
                contentTypeSelected: function (window, contentType) {
                    if (window) {
                        window.close();
                    }
                    if (contentType) {
                        this.createContent('contentType', contentType.get('qualifiedName'), contentType.get('name'));
                    }
                }
            }
        });

        this.application.on({});

    }

});