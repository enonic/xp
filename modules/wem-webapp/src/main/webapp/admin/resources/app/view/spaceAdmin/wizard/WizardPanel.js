Ext.define('Admin.view.spaceAdmin.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.spaceAdminWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.spaceAdmin.wizard.Toolbar',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.spaceAdmin.wizard.SpaceStepPanel'
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

        var iconUrl = 'resources/images/icons/128x128/default_space.png';
        var displayNameValue = 'Space Name';
        if (me.modelData) {
            displayNameValue = me.modelData.displayName || me.modelData.name;
            iconUrl = me.modelData.iconUrl;
        }

        me.headerData = {
            displayName: displayNameValue
        };

        me.tbar = Ext.createByAlias('widget.spaceAdminWizardToolbar', {});

        me.items = [
            {
                width: 121,
                padding: 9,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        photoUrl: iconUrl,
                        title: "Space",
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
                            top: '0px'
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
                        xtype: 'container',
                        cls: 'admin-wizard-header-container',
                        items: [
                            {
                                xtype: 'textfield',
                                itemId: 'displayName',
                                value: me.headerData ? me.headerData.displayName : undefined,
                                emptyText: 'Display Name',
                                enableKeyEvents: true,
                                cls: 'admin-display-name',
                                dirtyCls: 'admin-display-name-dirty'
                            }
                        ]
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: true,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
    },


    getSteps: function () {

        return [
            {
                xtype: 'spaceStepPanel',
                modelData: this.modelData
            },
            {
                stepTitle: 'Schemas'
            },
            {
                stepTitle: 'Modules'
            },
            {
                stepTitle: 'Summary'
            }
        ];
    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },


    getData: function () {
        return this.getWizardPanel().getData();
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.getWizardPanel(),
            iconRef = response.items && response.items.length > 0 && response.items[0].id;
        wizard.addData({iconRef: iconRef});
    }

});
