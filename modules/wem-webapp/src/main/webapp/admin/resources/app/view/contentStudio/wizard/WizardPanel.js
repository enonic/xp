Ext.define('Admin.view.contentStudio.wizard.WizardPanel', {
    extend: 'Admin.view.NewWizardPanel',
    alias: 'widget.contentStudioWizardPanel',
    requires: [
        'Admin.view.contentStudio.wizard.ConfigPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.contentStudio.wizard.Toolbar'
    ],
    border: 0,
    autoScroll: true,
    defaults: {
        border: false
    },

    initComponent: function () {
        var me = this;
        var isNew = me.isNewMode();

        me.tbar = this.getToolbar();

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
    },


    getWizardHeader: function () {
        return Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            nameConfig: {
                hidden: true
            },
            data: this.data
        });
    },

    getIcon: function () {
        var me = this;
        var headerData = this.resolveHeaderData(this.data);

        return {
            width: 100,
            height: 100,
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 100,
                    height: 100,
                    photoUrl: headerData.iconUrl,
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
                        zIndex: 1001,
                        top: '0'
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

    getToolbar: function () {
        var me = this;

        return Ext.createByAlias('widget.contentStudioWizardToolbar', {
            isNew: me.isNewMode()
        });
    },

    getSteps: function () {
        // override to add steps
        return [];
    },

    resolveHeaderData: function (data) {
        var iconUrl = 'resources/images/icons/128x128/cubes.png';
        var displayNameValue = 'Display Name';
        if (data) {
            displayNameValue = data.get('displayName') || data.get('name');
            iconUrl = data.get('iconUrl');
        }
        return {
            iconUrl: iconUrl,
            displayName: displayNameValue
        };
    },

    removeEmptySteps: function (wizardPanel) {
        wizardPanel.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.remove(item);
            }
        });
    },


    isNewMode: function () {
        return Ext.isDefined(this.isNew) ? this.isNew : Ext.isEmpty(this.data);
    },


    getWizardPanel: function () {
        return this.down('wizardPanel');
    },


    getData: function () {
        return Ext.apply(this.callParent(), this.down('wizardHeader').getData());
    },

    photoUploaded: function (photoUploadButton, response) {
        var iconRef = response.items && response.items.length > 0 && response.items[0].id;
        this.addData({iconRef: iconRef});
    }

});
