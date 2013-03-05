Ext.define('Admin.view.contentStudio.wizard.RelationshipTypeWizardPanel', {
    extend: 'Admin.view.contentStudio.wizard.WizardPanel',
    alias: 'widget.contentStudioRelationshipTypeWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.contentStudioWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'relationshipType'
        });
    },

    getSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Relationship Type',
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
