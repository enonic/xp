Ext.define( 'Admin.view.datadesigner.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dataDesignerWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.datadesigner.wizard.GeneralPanel',
        'Admin.view.datadesigner.wizard.ConfigPanel',
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
        var steps = me.getSteps();
        var isNew = this.isNewContentType();
        /*
        var displayNameValue = isNew ? 'Display name' : me.modelData.name;
        var steps = me.getSteps();
        var groupWizardHeader = Ext.create( 'Ext.container.Container', {
            itemId: 'wizardHeader',
            autoHeight: true,
            cls: 'cms-wizard-header-container',
            border: false,
            tpl: new Ext.XTemplate(Templates.account.groupWizardHeader),
            data: {
                displayName: displayNameValue
            }
        } );
        */

        /*
        me.tbar = Ext.createByAlias( 'widget.dataDesignerWizardToolbar', {
            isNew: isNew
        } );
        */

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
                        cls: 'icon-data-designer-128',
                        listeners: {
                            render: function( cmp ) {
                                Ext.tip.QuickTipManager.register({
                                    target: cmp.el,
                                    text: 'Content type',
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
                    ,
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
            //me.removeEmptySteps( groupWizard.getWizardPanel() );
        });

    },


    getSteps: function()
    {
        var me = this;
        var nameStep = {
            stepTitle: "General",
            modelData: me.modelData,
            xtype: 'dataDesignerWizardGeneralPanel'
        };
        var configStep = {
            stepTitle: "Config",
            modelData: me.modelData,
            xtype: 'dataDesignerWizardConfigPanel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            modelData: me.modelData,
            xtype: 'summaryTreePanel'
        };

        return [nameStep, configStep, summaryStep];
    },


    removeEmptySteps: function( wizardPanel ) {
        wizardPanel.items.each( function( item ){
            if ( !item.alwaysKeep && item.getForm && ( item.getForm().getFields().getCount() == 0  ))
            {
                wizardPanel.remove( item );
            }
        });
    },


    isNewContentType: function()
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
