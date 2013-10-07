Ext.define('Admin.view.schemaManager.wizard.WizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.schemaManagerWizardPanel',
    requires: [
        'Admin.view.schemaManager.wizard.ConfigPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.schemaManager.wizard.Toolbar'
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


    createWizardHeader: function () {
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            pathConfig: {
                hidden: true
            },
            nameConfig: {
                hidden: true
            },
            data: this.data
        });

        this.validateItems.push(wizardHeader);

        return wizardHeader;
    },

    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Save'
        };
    },

    createIcon: function () {
        var me = this;
        var headerData = this.resolveHeaderData(this.data);

        return {
            width: 110,
            height: 110,
            items: [
                {
                    xtype: 'photoUploadButton',
                    width: 110,
                    height: 110,
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
                        top: '5px'
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

        return Ext.createByAlias('widget.schemaManagerWizardToolbar', {
            isNew: me.isNewMode()
        });
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

    getWizardHeader: function () {
        return this.down('wizardHeader');
    },

    getData: function () {
        return Ext.apply(this.callParent(), this.getWizardHeader().getData());
    },

    photoUploaded: function (photoUploadButton, response) {
        var iconRef = response.items && response.items.length > 0 && response.items[0].id;
        this.addData({iconRef: iconRef});
    }

});
