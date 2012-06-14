Ext.define( 'Admin.view.userstore.wizard.UserstoreWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstoreWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.userstore.wizard.UserstoreWizardToolbar',
        'Admin.view.userstore.wizard.UserstoreWizardConfigPanel',
        'Admin.view.userstore.wizard.UserstoreWizardGeneralPanel',
        'Admin.view.userstore.wizard.UserstoreWizardAdminPanel',
        'Admin.view.SummaryTreePanel'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    initComponent: function()
    {
        var me = this;
        var isNew = this.isNewUserstore();
        var displayNameValue = isNew ? 'Display name' : me.modelData.name;
        var steps = me.getSteps();
        var groupWizardHeader = Ext.create( 'Ext.container.Container', {
            itemId: 'wizardHeader',
            autoHeight: true,
            cls: 'admin-wizard-header-container',
            border: false,
            tpl: new Ext.XTemplate(Templates.account.groupWizardHeader),
            data: {
                displayName: displayNameValue
            }
        } );

        me.tbar = Ext.createByAlias( 'widget.userstoreWizardToolbar', {
            isNew: isNew
        } );

        me.items = [
            {
                width: 138,
                padding: 5,
                border: false,
                items: [
                    {
                        xtype: 'container',
                        plain: true,
                        width: 128,
                        height: 128,
                        cls: 'icon-userstore-128',
                        listeners: {
                            render: function( cmp ) {
                                Ext.tip.QuickTipManager.register({
                                    target: cmp.el,
                                    text: 'Userstore',
                                    width: 100,
                                    dismissDelay: 10000
                                });
                            }
                        }
                    }
                ]
            },
            {
                columnWidth: 1,
                padding: '10 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    groupWizardHeader,
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: isNew,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent( arguments );

        this.on( 'afterrender', function( groupWizard ) {
            me.removeEmptySteps( groupWizard.getWizardPanel() );
        });

    },

    getSteps: function()
    {
        var me = this;
        var generalStep = {
            stepTitle: "General",
            modelData: me.modelData,
            xtype: 'userstoreWizardGeneralPanel'
        };
        var configStep = {
            stepTitle: "Config",
            modelData: me.modelData,
            xtype: 'userstoreWizardConfigPanel'
        };
        var adminStep = {
            stepTitle: 'Administrators',
            modelData: me.modelData,
            xtype: 'userstoreWizardAdminPanel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            modelData: me.modelData,
            xtype: 'summaryTreePanel'
        };

        return [generalStep, configStep, adminStep, summaryStep];
    },

    removeEmptySteps: function( wizardPanel ) {
        wizardPanel.items.each( function( item ){
            if ( !item.alwaysKeep && item.getForm && ( item.getForm().getFields().getCount() == 0  ))
            {
                wizardPanel.remove( item );
            }
        });
    },

    isNewUserstore: function()
    {
        return this.modelData == undefined;
    },

    getWizardPanel: function() {
        return this.down('wizardPanel');
    },

    getData: function()
    {
        return this.getWizardPanel().getData();
    }

} );
