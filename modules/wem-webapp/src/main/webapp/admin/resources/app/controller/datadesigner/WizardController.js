Ext.define('Admin.controller.datadesigner.WizardController', {
    extend: 'Admin.controller.datadesigner.Controller',

    stores: [],

    models: [],

    views: [],

    init: function () {
        this.control({
            'dataDesignerWizardPanel *[action=closeWizard]': {
                click: this.closeWizard
            },
            'dataDesignerWizardPanel *[action=deleteContentType]': {
                click: function () {
                    var contentType = this.getWizardData();
                    this.showDeleteContentTypeWindow(contentType);
                }
            }
        });
    },

    closeWizard: function () {
        this.getCurrentTab().close();
    },

    getWizardData: function () {
        return this.getCurrentTab().getData();
    }

});