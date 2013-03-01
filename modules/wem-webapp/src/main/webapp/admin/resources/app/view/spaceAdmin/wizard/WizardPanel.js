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

    headerData: {},

    initComponent: function () {
        var me = this;
        var steps = me.getSteps();

        var iconUrl = 'resources/images/icons/128x128/default_space.png';
        var displayNameValue = '';
        var spaceName = '';
        if (me.modelData) {
            displayNameValue = me.modelData.displayName || '';
            spaceName = me.modelData.name || '';
            iconUrl = me.modelData.iconUrl;
        }

        me.headerData = {
            'displayName': displayNameValue,
            'spaceName': spaceName,
            'isNewSpace': spaceName? false : true
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
                                colspan: 2,
                                itemId: 'displayName',
                                value: me.headerData.displayName,
                                emptyText: 'Display Name',
                                enableKeyEvents: true,
                                cls: 'admin-display-name',
                                hideLabel: true,
                                dirtyCls: 'admin-display-name-dirty'
                            },
                            {
                                xtype: 'component',
                                itemId: 'spaceName',
                                cls: 'admin-content-path',
                                data: me.headerData,
                                tpl: '<table><tr>' +
                                     '<td class="fluid"><input type="text" value="{spaceName}" placeholder="Name" {[values.isNewSpace ? "" : "readonly"]}/></td>' +
                                     '</tr></table>'
                            }
                        ]
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: me.headerData.isNewSpace,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
    },

    listeners : {
        activate: function() {
            this.down('#displayName').focus(false, 100);
        }
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
                stepTitle: 'Templates'
            },
            {
                stepTitle: 'Security'
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
        var data = this.getWizardPanel().getData();
        data.displayName = this.down('#displayName').getValue();
        data.spaceName = this.down('#spaceName').el.down('input').getValue();
        return data;
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.getWizardPanel(),
            iconRef = response.items && response.items.length > 0 && response.items[0].id;
        wizard.addData({iconRef: iconRef});
    }

});
