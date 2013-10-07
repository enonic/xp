Ext.define('Admin.view.schemaManager.wizard.RelationshipTypeWizardPanel', {
    extend: 'Admin.view.schemaManager.wizard.WizardPanel',
    alias: 'widget.schemaManagerRelationshipTypeWizardPanel',


    getToolbar: function () {
        return Ext.createByAlias('widget.schemaManagerWizardToolbar', {
            isNew: this.isNewMode(),
            schema: 'relationshipType'
        });
    },

    createSteps: function () {
        var me = this;

        var configStep = {
            stepTitle: 'Relationship Type',
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
