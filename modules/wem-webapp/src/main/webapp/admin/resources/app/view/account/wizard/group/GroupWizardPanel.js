Ext.define( 'Admin.view.account.wizard.group.GroupWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.account.wizard.group.GroupWizardToolbar',
        'Admin.view.account.wizard.group.WizardStepGeneralPanel',
        'Admin.view.account.wizard.group.WizardStepMembersPanel',
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
        var isNew = this.isNewGroup();
        var isRole = this.isRole();
        var displayNameValue = isNew ? 'Display name' : me.modelData.displayName;
        var qualifiedName = isNew ? me.userstore + '\\' : me.modelData.qualifiedName;
        var steps = me.getSteps();
        var groupWizardHeader = Ext.create( 'Ext.container.Container', {
            itemId: 'wizardHeader',
            autoHeight: true,
            cls: 'cms-wizard-header-container',
            border: false,
            tpl: new Ext.XTemplate( Templates.account.groupWizardHeader ),
            data: {
                displayName: displayNameValue,
                qualifiedName: qualifiedName
            }
        } );

        me.tbar = Ext.createByAlias( 'widget.groupWizardToolbar', {
            xtype: 'groupWizardToolbar',
            isNew: isNew,
            isRole: isRole
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
                        cls: me.modelData &&
                             (me.modelData.type === 'role') ? 'icon-role-128' : 'icon-group-128',
                        listeners: {
                            render: function( cmp )
                            {
                                Ext.tip.QuickTipManager.register( {
                                    target: cmp.el,
                                    text: me.modelData ? Ext.String.capitalize( me.modelData.type ) : 'Group',
                                    width: 100,
                                    dismissDelay: 10000 // Hide after 10 seconds hover
                                } );
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

        this.on( 'afterrender', function( groupWizard )
        {
            me.removeEmptySteps( groupWizard.getWizardPanel() );
        } );

        if ( me.modelData ) {
            var wizard = me.down( 'wizardPanel' );
            wizard.addData( {userStore: me.modelData.userStore} );
            wizard.addData( {key: me.modelData.key} );
            wizard.addData( {'displayName': me.modelData.displayName} );
            wizard.addData( {builtIn: me.modelData.type === 'role'} );
        }

    },

    getSteps: function()
    {
        var me = this;
        var isRole = me.modelData != undefined ? me.modelData.type === 'role' : false;
        var generalStep = {
            stepTitle: "General",
            modelData: this.modelData,
            xtype: 'wizardStepGeneralPanel'
        };
        var membersStep = {
            stepTitle: "Members",
            modelData: this.modelData,
            userStore: this.userstore,
            xtype: 'wizardStepMembersPanel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            modelData: this.modelData,
            xtype: 'summaryTreePanel'
        }

        if ( isRole ) {
            return [membersStep, summaryStep];
        }
        else {
            return [generalStep, membersStep, summaryStep];
        }

    },

    removeEmptySteps: function( wizardPanel )
    {
        wizardPanel.items.each( function( item )
        {
            if ( !item.alwaysKeep && item.getForm && ( item.getForm().getFields().getCount() == 0  ) ) {
                wizardPanel.remove( item );
            }
        } );
    },

    isNewGroup: function()
    {
        return this.modelData == undefined;
    },

    isRole: function()
    {
        return this.modelData && this.modelData.type == 'role';
    },

    getWizardPanel: function()
    {
        return this.down( 'wizardPanel' );
    },

    getData: function()
    {
        return this.getWizardPanel().getData();
    }

} );
