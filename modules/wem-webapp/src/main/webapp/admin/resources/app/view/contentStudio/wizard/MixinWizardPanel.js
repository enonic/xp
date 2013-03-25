Ext.define('Admin.view.contentStudio.wizard.MixinWizardPanel', {
    extend: 'Admin.view.contentStudio.wizard.WizardPanel',
    alias: 'widget.contentStudioMixinWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.contentStudioWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'mixin'
        });
    },

    createSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Mixin',
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
