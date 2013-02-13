Ext.define('Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentWizardPanel',
    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.contentManager.wizard.ContentWizardToolbar',
        'Admin.view.contentManager.wizard.ContentDataPanel'
    ],

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    header: false,

    border: 0,
    autoScroll: true,

    // split modes 0 - form, 1 - split, 2 - live
    splitMode: 0,

    defaults: {
        border: false
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
            xtype: 'contentWizardToolbar'
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

        var splitter = {
            itemId: 'splitter',
            xtype: 'splitter',
            listeners: {
                move: function (el, x, y, opts) {
                    me.repositionWizardNavButton(x);
                },

                hide: function () {
                    me.resetWizardNavButton();
                }
            }
        };

        this.items = [wizardPanel, splitter, liveEdit];
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

    cycleLiveEdit: function () {
        // cycle mode
        var me = this;
        if (this.splitMode === 2) {
            this.splitMode = 0;
        } else {
            this.splitMode += 1;
        }

        var wizardPanel = this.down('#wizardPanel');
        var livePreview = this.down('#livePreview');
        var splitter = this.down('#splitter');
        switch (this.splitMode) {
        case 0:
            //form
            wizardPanel.show();
            livePreview.hide();
            break;
        case 1:
            // split
            //TODO update urls when they are ready
            livePreview.load('/dev/live-edit/page/page.jsp', true);
            splitter.show();
            livePreview.show();
            break;
        case 2:
            // live
            splitter.hide();
            wizardPanel.hide();
            break;
        }
        return this.splitMode;
    },

    resetWizardNavButton: function () {
        var nextButton = this.down("#next");
        nextButton.el.setRight(30 + "px");
        nextButton.el.setLeft("auto");
        nextButton.el.setWidth("64px");
    },

    // Hack to make arrow move when using split edit. Possibly not a great solution.
    repositionWizardNavButton: function (offset) {
        var baseOffset = -92; // Offset is 30 from right side converted to left (-60) minus half of the width of the button (32)
        var nextButton = this.down("#next");
        offset += baseOffset;
        nextButton.el.setLeft(offset + "px");
        // For some reason the width is changed when the offset is changed, hardcoded to change it back
        nextButton.el.setWidth("64px");
    }

});
