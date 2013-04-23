Ext.define('Admin.view.schemaManager.wizard.MixinWizardPanel', {
    extend: 'Admin.view.schemaManager.wizard.WizardPanel',
    alias: 'widget.schemaManagerMixinWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.schemaManagerWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'mixin'
        });
    },

    createSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Mixin',
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
