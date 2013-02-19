Ext.define('Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentWizardPanel',

    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.contentManager.wizard.ContentWizardToolbar',
        'Admin.view.contentManager.wizard.ContentDataPanel'
    ],

    layout: {
        type: 'card'
    },

    header: false,

    border: 0,
    autoScroll: true,

    isLiveMode: false,

    defaults: {
        border: false
    },

    listeners: {
        afterrender: function () {
            this.setLiveMode(this.isLiveMode);

            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/page.jsp?edit=true');
            }
        }
    },

    initComponent: function () {
        var me = this;

        this.headerData = {
            imageUrl: (this.data && this.data.content) ? this.data.content.iconUrl : undefined,
            displayName: (this.data && this.data.content) ? this.data.content.displayName : 'New Content',
            contentPath: (this.data && this.data.content) ? this.data.content.path : this.data.contentParent ? this.data.contentParent.path
                : '',
            contentAssignee: 'Thomas Sigdestad',
            contentStatus: 'Draft'
        };

        me.tbar = Ext.createByAlias('widget.contentWizardToolbar', {
            isLiveMode: this.isLiveMode
        });

        var wizardPanel = {
            flex: 1,
            layout: 'column',
            itemId: 'wizardPanel',
            border: false,
            autoScroll: true,

            defaults: {
                border: false
            },
            items: [
                {
                    width: 110,
                    padding: 5,
                    border: false,
                    items: [
                        {
                            xtype: 'image',
                            width: 100,
                            height: 100,
                            src: me.headerData.imageUrl,
                            listeners: {
                                render: function (cmp) {

                                    var contentType = (me.data && me.data.contentType) ? me.data.contentType : undefined;
                                    if (contentType) {
                                        var toolText = '<strong>' + contentType.displayName + '</strong></br>' +
                                                       contentType.module + ':' + contentType.name;

                                        var tip = Ext.create('Ext.tip.ToolTip', {
                                            target: cmp.el,
                                            html: toolText,
                                            padding: 10,
                                            styleHtmlContent: true,
                                            dismissDelay: 10000 // Hide after 10 seconds hover
                                        });
                                    }
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
                                    value: me.headerData.displayName,
                                    emptyText: 'Display Name',
                                    enableKeyEvents: true,
                                    cls: 'admin-display-name',
                                    dirtyCls: 'admin-display-name-dirty'
                                },
                                {
                                    xtype: 'container',
                                    itemId: 'wizardHeader',
                                    styleHtmlContent: true,
                                    autoHeight: true,
                                    cls: 'admin-wizard-header-container',
                                    tpl: new Ext.XTemplate(Templates.contentManager.contentWizardHeader),
                                    data: me.headerData
                                }
                            ]
                        },
                        {
                            xtype: 'wizardPanel',
                            showControls: true,
                            items: me.getSteps()
                        }
                    ]
                }
            ]
        };

        var liveEdit = {
            flex: 1,
            itemId: 'livePreview',
            xtype: 'contentLive',
            border: false,
            hidden: true
        };

        this.items = [wizardPanel, liveEdit];
        this.callParent(arguments);

    },

    getSteps: function () {
        var dataStep = {
            stepTitle: ( this.data && this.data.contentType ) ? this.data.contentType.displayName : "Data",
            xtype: 'contentDataPanel',
            contentType: this.data ? this.data.contentType : undefined,
            content: this.data ? this.data.content : null
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

        return [dataStep, metaStep, securityStep, summaryStep];

    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        var wizardData = {displayName: '', contentData: {}};
        var contentData = this.getWizardPanel().getData();
        var displayNameField = this.down('#displayName');
        if (displayNameField) {
            wizardData.displayName = displayNameField.getValue();
        }
        wizardData.contentData = contentData;
        return wizardData;
    },

    setLiveMode: function (mode) {
        this.getLayout().setActiveItem(mode ? 1 : 0);

        if (mode) {
            var livePreview = this.down('#livePreview');
            //TODO update urls when they are ready
            livePreview.load('/dev/live-edit/page/page.jsp?edit=true');
        }
    },

    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;

        this.setLiveMode(this.isLiveMode);
    }
});
