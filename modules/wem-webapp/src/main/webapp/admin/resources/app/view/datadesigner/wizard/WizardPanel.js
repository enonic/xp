Ext.define('Admin.view.datadesigner.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dataDesignerWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.datadesigner.wizard.GeneralPanel',
        'Admin.view.datadesigner.wizard.ConfigPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton'
    ],
    layout: 'column',
    border: 0,
    autoScroll: true,
    defaults: {
        border: false
    },


    initComponent: function () {
        var me = this;
        var steps = me.getSteps();
        var isNew = this.isNewContentType();
        /*
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
         */

        /*
         me.tbar = Ext.createByAlias( 'widget.dataDesignerWizardToolbar', {
         isNew: isNew
         } );
         */

        me.items = [
            {
                width: 121,
                padding: 9,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        photoUrl: "resources/images/icons/128x128/cubes.png",
                        title: "Content",
                        style: {
                            margin: '1px'
                        },
                        progressBarHeight: 6,
                        listeners: {
                            mouseenter: function () {
                                var imageToolTip = me.down('#imageToolTip');
                                imageToolTip.show();
                            },
                            mouseleave: function () {
                                var imageToolTip = me.down('#imageToolTip');
                                imageToolTip.hide();
                            }
                        }
                    },
                    {
                        styleHtmlContent: true,
                        height: 50,
                        border: 0,
                        itemId: 'imageToolTip',
                        style: {
                            top: '141px',
                            left: '10px'
                        },
                        cls: 'admin-image-upload-button-image-tip',
                        html: '<div class="x-tip x-tip-default x-layer" role="tooltip">' +
                              '<div class="x-tip-anchor x-tip-anchor-top"></div>' +
                              '<div class="x-tip-body  x-tip-body-default x-tip-body-default">' +
                              'Click to upload icon</div></div>',
                        listeners: {
                            afterrender: function (cmp) {
                                Ext.Function.defer(function () {
                                    cmp.hide();
                                }, 10000);
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
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: isNew,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        this.on('afterrender', function (groupWizard) {
            //me.removeEmptySteps( groupWizard.getWizardPanel() );
        });

    },


    getSteps: function () {
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


    removeEmptySteps: function (wizardPanel) {
        wizardPanel.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.remove(item);
            }
        });
    },


    isNewContentType: function () {
        return this.modelData == undefined;
    },


    getWizardPanel: function () {
        return this.down('wizardPanel');
    },


    getData: function () {
        return this.getWizardPanel().getData();
    }

});
