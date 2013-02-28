Ext.define('Admin.view.contentStudio.wizard.ContentTypeWizardPanel', {
    extend: 'Admin.view.contentStudio.wizard.WizardPanel',
    alias: 'widget.contentStudioContentTypeWizardPanel',


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
