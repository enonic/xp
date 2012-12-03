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
            displayName: (this.data && this.data.content) ? this.data.content.displayName : 'New Content',
            contentType: (this.data && this.data.contentType) ? this.data.contentType.qualifiedName : undefined,
            contentPath: (this.data && this.data.content) ? this.data.content.path : ''
        };
        var contentWizardHeader = Ext.create('Ext.container.Container', {
            itemId: 'wizardHeader',
            styleHtmlContent: true,
            autoHeight: true,
            cls: 'admin-wizard-header-container',
            labelCls: 'label',
            listeners: {
                afterrender: {
                    fn: function () {
                        var me = this;
                        me.getEl().addListener('click', function (event, target, eOpts) {
                            me.toggleDisplayNameField(event, target);
                        });
                    },
                    scope: this
                }
            },
            tpl: new Ext.XTemplate(Templates.contentManager.contentWizardHeader),
            data: me.headerData
        });

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
                    width: 138,
                    padding: 5,
                    border: false,
                    items: [
                        {
                            xtype: 'container',
                            plain: true,
                            width: 128,
                            height: 128,
                            cls: 'icon-content-128',
                            listeners: {
                                render: function (cmp) {
                                    Ext.tip.QuickTipManager.register({
                                        target: cmp.el,
                                        text: 'Content',
                                        width: 100,
                                        dismissDelay: 10000 // Hide after 10 seconds hover
                                    });
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
                        contentWizardHeader,
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
            xtype: 'component',
            width: 5,
            style: 'background-color: #DFE8F6;'
        };

        this.items = [wizardPanel, splitter, liveEdit];
        this.callParent(arguments);

    },

    getSteps: function () {
        var dataStep = {
            stepTitle: "Data",
            xtype: 'contentDataPanel',
            contentType: this.data ? this.data.contentType : undefined,
            content: this.data ? this.data.content : null
        };
        var pageStep = {
            stepTitle: 'Page',
            xtype: 'panel'
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

        return [dataStep, pageStep, metaStep, securityStep, summaryStep];

    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        return this.getWizardPanel().getData();
    },

    toggleDisplayNameField: function (event, target) {
        var clickedElement = new Ext.Element(target);
        var parentToClickedElementIsHeader = clickedElement.findParent('.admin-wizard-header');
        var displayNameField = Ext.DomQuery.select('input.admin-display-name', this.getEl().dom)[0];
        var displayNameFieldElement = new Ext.Element(displayNameField);

        if (parentToClickedElementIsHeader) {
            displayNameFieldElement.dom.removeAttribute('readonly');
            displayNameFieldElement.addCls('admin-edited-field');
        } else {
            displayNameFieldElement.set({readonly: true});
            var value = Ext.String.trim(displayNameFieldElement.getValue());
            if (value === '' || value === 'Display Name') {
                displayNameFieldElement.removeCls('admin-edited-field');
            }
        }
    },

    cycleLiveEdit: function () {
        // cycle mode
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
            var url = this.data ? this.data.get('url') : '';
            livePreview.load(url, true);
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
    }

});
