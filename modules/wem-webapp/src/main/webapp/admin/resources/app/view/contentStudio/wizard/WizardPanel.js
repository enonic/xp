Ext.define('Admin.view.contentStudio.wizard.WizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentStudioWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.contentStudio.wizard.ConfigPanel',
        'Admin.view.SummaryTreePanel',
        'Admin.plugin.fileupload.PhotoUploadButton',
        'Admin.view.contentStudio.wizard.Toolbar'
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
        var isNew = me.isNewMode();

        var headerData = this.resolveHeaderData(this.data);

        me.tbar = this.getToolbar();

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
                        xtype: 'container',
                        cls: 'admin-wizard-header-container',
                        items: [
                            {
                                xtype: 'textfield',
                                itemId: 'displayName',
                                value: headerData.displayName,
                                emptyText: 'Display Name',
                                enableKeyEvents: true,
                                cls: 'admin-display-name',
                                dirtyCls: 'admin-display-name-dirty'
                            }
                        ]
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: false,
                        isNew: isNew,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        var uploader = this.down('photoUploadButton');
        uploader.on('fileuploaded', me.photoUploaded, me);
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
        return this.getWizardPanel().getData();
    },

    photoUploaded: function (photoUploadButton, response) {
        var wizard = this.down('wizardPanel'),
            iconRef = response.items && response.items.length > 0 && response.items[0].id;
        wizard.addData({iconRef: iconRef});
    }

});
