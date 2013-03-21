Ext.define('Admin.view.spaceAdmin.wizard.WizardPanel', {
    extend: 'Admin.view.NewWizardPanel',
    alias: 'widget.spaceAdminWizardPanel',
    requires: [
        'Admin.view.WizardHeader',
        'Admin.view.spaceAdmin.wizard.Toolbar',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.spaceAdmin.wizard.SpaceStepPanel'
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

    getWizardHeader: function () {
        return Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            data: this.data
        });
    },

    getIcon: function () {
        var me = this;
        var headerData = me.resolveHeaderData(me.data);

        return {
            xtype: 'container',
            width: 100,
            height: 100,
            padding: 5,
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 100,
                    height: 100,
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
                        top: '0px',
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


    getData: function () {
        var data = this.callParent();
        var headerData = this.down('wizardHeader').getData();

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
