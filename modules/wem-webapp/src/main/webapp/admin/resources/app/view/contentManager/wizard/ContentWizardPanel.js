Ext.define( 'Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.contentManager.wizard.ContentWizardToolbar'
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
        me.headerData = {
            displayName: 'New Content'
        };
        var contentWizardHeader = Ext.create( 'Ext.container.Container',  {
            itemId: 'wizardHeader',
            styleHtmlContent: true,
            autoHeight: true,
            cls: 'cms-wizard-header-container',
            listeners: {
                afterrender: {
                    fn: function()
                    {
                        var me = this;
                        me.getEl().addListener( 'click', function( event, target, eOpts )
                        {
                            me.toggleDisplayNameField( event, target );
                        } );
                    },
                    scope: this
                }
            },
            tpl: new Ext.XTemplate(Templates.contentManager.contentWizardHeader),
            data: me.headerData
        } );

        me.tbar = Ext.createByAlias( 'widget.contentWizardToolbar', {
            xtype: 'contentWizardToolbar'
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
                        cls: 'icon-content-128',
                        listeners: {
                            render: function( cmp )
                            {
                                Ext.tip.QuickTipManager.register( {
                                    target: cmp.el,
                                    text: 'Content',
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
                    contentWizardHeader,
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        items: me.getSteps()
                    }
                ]
            }
        ];

        this.callParent( arguments );

    },

    getSteps: function()
    {
        var dataStep = {
            stepTitle: "Data",
            xtype: 'panel'
        };
        var treeStep = {
            stepTitle: "Tree",
            xtype: 'panel'
        };
        var pageStep = {
            stepTitle: 'Page',
            xtype: 'panel'
        };
        var metaStep = {
            stepTitle: 'Meta',
            xtype: 'panel'
        };
        var securityStep = {
            stepTitle: 'Security',
            xtype: 'panel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            xtype: 'panel'
        };

        return [dataStep, treeStep, pageStep, metaStep, securityStep, summaryStep];

    },

    getWizardPanel: function()
    {
        return this.down( 'wizardPanel' );
    },

    getData: function()
    {
        return this.getWizardPanel().getData();
    },

    toggleDisplayNameField: function( event, target )
    {
        var clickedElement = new Ext.Element( target );
        var parentToClickedElementIsHeader = clickedElement.findParent( '.cms-wizard-header' );
        var displayNameField = Ext.DomQuery.select( 'input.cms-display-name', this.getEl().dom )[0];
        var displayNameFieldElement = new Ext.Element( displayNameField );

        if ( parentToClickedElementIsHeader )
        {
            displayNameFieldElement.dom.removeAttribute( 'readonly' );
            displayNameFieldElement.addCls( 'cms-edited-field' );
        }
        else
        {
            displayNameFieldElement.set( {readonly: true} );
            var value = Ext.String.trim( displayNameFieldElement.getValue() );
            if ( value === '' || value === 'Display Name' )
            {
                displayNameFieldElement.removeCls( 'cms-edited-field' );
            }
        }
    }

} );
