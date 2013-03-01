Ext.define('Admin.view.contentStudio.wizard.ContentTypeWizardPanel', {
    extend: 'Admin.view.contentStudio.wizard.WizardPanel',
    alias: 'widget.contentStudioContentTypeWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.contentStudioWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'contentType'
        });
    },

    getSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Content Type',
            data: me.data,
            xtype: 'contentStudioWizardConfigPanel',
            listeners: {
                afterrender: function (panel) {
                    me.panelRendered = true;
                }
            }
        };

        return [configStep];
    }

});
