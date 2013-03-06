Ext.define('Admin.view.spaceAdmin.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.spaceAdminWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.WizardHeader',
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

        var headerData = me.resolveHeaderData(me.data);

        me.tbar = Ext.createByAlias('widget.spaceAdminWizardToolbar', {
            isNew: headerData.isNewSpace
        });

        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            data: me.data
        });

        me.items = [
            {
                width: 121,
                padding: 9,
                items: [
                    {
                        xtype: 'photoUploadButton',
                        width: 111,
                        height: 111,
                        photoUrl: headerData.iconUrl,
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
                    wizardHeader,
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        validateItems: [wizardHeader],
                        isNew: headerData.isNewSpace,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
    },

    resolveHeaderData: function (data) {
        var me = this;
        var iconUrl = 'resources/images/icons/128x128/default_space.png';
        var displayNameValue = '';
        var spaceName = '';
        if (data) {
            displayNameValue = me.data.get('displayName') || '';
            spaceName = me.data.get('name') || '';
            iconUrl = me.data.get('iconUrl');
        }

        return {
            'displayName': displayNameValue,
            'spaceName': spaceName,
            'isNewSpace': spaceName ? false : true,
            'iconUrl': iconUrl
        };
    },

    getSteps: function () {

        return [
            {
                xtype: 'spaceStepPanel',
                data: this.data
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

    getWizardHeader: function() {
        return this.down('wizardHeader');
    },


    getData: function () {
        var data = this.getWizardPanel().getData();
        var headerData = this.getWizardHeader().getData();

        return Ext.apply(data, {
            displayName: headerData.displayName,
            spaceName: headerData.name
        });
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.getWizardPanel(),
            iconRef = response.items && response.items.length > 0 && response.items[0].id;
        wizard.addData({iconRef: iconRef});
    }

});
