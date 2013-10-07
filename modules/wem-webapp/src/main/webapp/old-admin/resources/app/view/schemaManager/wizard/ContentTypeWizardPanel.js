Ext.define('Admin.view.schemaManager.wizard.ContentTypeWizardPanel', {
    extend: 'Admin.view.schemaManager.wizard.WizardPanel',
    alias: 'widget.schemaManagerContentTypeWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.schemaManagerWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'contentType'
        });
    },

    createSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Content Type',
            data: me.data,
            xtype: 'schemaManagerWizardConfigPanel',
            listeners: {
                afterrender: function (panel) {
                    me.panelRendered = true;
                }
            }
        };

        return [configStep];
    }

});
