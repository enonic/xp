Ext.define('Admin.view.wizard.WizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.spaceAdminWizardPanel',
    requires: [
        'Admin.view.WizardHeader',
        'Admin.view.wizard.Toolbar',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.wizard.SpaceStepPanel'
    ],
    border: 0,
    autoScroll: true,
    defaults: {
        border: false
    },


    initComponent: function () {
        var me = this;

        var headerData = me.resolveHeaderData(me.data);

        me.tbar = Ext.createByAlias('widget.spaceAdminWizardToolbar', {
            isNew: headerData.isNewSpace
        });

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

    createSteps: function () {

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

    createWizardHeader: function () {
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            data: this.data
        });
        this.validateItems.push(wizardHeader);
        return wizardHeader;
    },

    createIcon: function () {
        var me = this;
        var headerData = me.resolveHeaderData(me.data);

        return {
            xtype: 'container',
            width: 110,
            height: 110,
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 110,
                    height: 110,
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
                        top: '5px',
                        zIndex: 1001
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
        };
    },

    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Save',
            action: 'saveSpace'
        };
    },

    getWizardHeader: function () {
        return this.down('wizardHeader');
    },

    getData: function () {
        var data = this.callParent();
        var headerData = this.getWizardHeader().getData();

        return Ext.apply(data, {
            displayName: headerData.displayName,
            spaceName: headerData.name
        });
    },

    photoUploaded: function (photoUploadButton, response) {
        var iconRef = response.items && response.items.length > 0 && response.items[0].id;
        this.addData({iconRef: iconRef});
    }

});
