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
    autoScroll: false,
    evaluateDisplayName: true,

    isLiveMode: false,

    defaults: {
        border: false
    },

    listeners: {
        afterrender: function () {
            this.setLiveMode(this.isLiveMode);
        },
        copyremoved: function (copy) {
            console.log(copy.getValue());
            var wizard = this.getWizardPanel();
            var data = wizard.getData();
            var copyData = copy.getValue();
            if (copyData instanceof Array) {
                Ext.each(copyData, function (copyDataItem) {
                    wizard.deleteData(copyDataItem.path);
                });
            } else {
                wizard.deleteData(copyData.path);
            }
        }
    },

    initComponent: function () {
        var me = this;

        if (this.data.content && !Ext.isEmpty(this.data.content.path)) {
            this.evaluateDisplayName = false;
        }

        var headerData = this.prepareHeaderData(this.data);

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
                            src: headerData.imageUrl,
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
                                    colspan: 2,
                                    itemId: 'displayName',
                                    hideLabel: true,
                                    value: headerData.displayName,
                                    emptyText: 'Display Name',
                                    cls: 'admin-display-name',
                                    dirtyCls: 'admin-display-name-dirty',
                                    enableKeyEvents: true,
                                    listeners: {
                                        change: function (f, e) {
                                            me.onDisplayNameChanged(f, e);
                                        }
                                    }
                                },
                                {
                                    xtype: 'component',
                                    itemId: 'contentPath',
                                    cls: 'admin-content-path',
                                    data: headerData,
                                    tpl: '<table><tr>' +
                                         '<td><span>{contentPath}</span></td>' +
                                         '<td class="fluid"><input type="text" value="{contentName}" {[values.isRoot ? "readonly" : ""]}/></td>' +
                                         '</tr></table>'
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

    prepareHeaderData: function (data) {
        var contentPath = '';
        var isRoot = false;
        var isNew = true;

        if (data.content) {
            if (!Ext.isEmpty(data.content.path)) {
                contentPath = data.content.path;
                isNew = false;
            }
            if (Ext.isDefined(data.content.isRoot)) {
                isRoot = data.content.isRoot;
            }
        }
        if (isNew && data.contentParent) {
            if (!Ext.isEmpty(data.contentParent.path)) {
                // content is deletable if not root
                var isParentRoot = !data.contentParent.deletable || false;
                contentPath = data.contentParent.path + (isParentRoot ? '' : '/');
            }
        }

        var lastSlashIndex = contentPath.lastIndexOf('/');

        var contentName = '/';
        if (lastSlashIndex >= 0) {
            // consider last / as name in case it is root, or part of the path otherwise
            contentName = contentPath.substring(lastSlashIndex + (isRoot ? 0 : 1));
            contentPath = contentPath.substring(0, lastSlashIndex + (isRoot ? 0 : 1));
        }

        return {
            imageUrl: (data && data.content) ? data.content.iconUrl : undefined,
            displayName: (data && data.content) ? data.content.displayName : 'New Content',
            contentPath: contentPath,
            contentName: contentName,
            isRoot: isRoot
        };
    },

    onDisplayNameChanged: function (f, e) {
        this.evaluateDisplayName = false;
    },

    onFormInputChanged: function (f, e) {
        if (this.evaluateDisplayName) {
            var displayNameField = this.down('#displayName');

            if (displayNameField) {
                var rawData = this.getData().contentData;
                var contentData = {};

                var key;
                for (key in rawData) {
                    if (rawData.hasOwnProperty(key)) {
                        contentData[key.replace(/\[0\]/g, '')] = rawData[key];
                    }
                }

                var fn = this.data.contentType.contentDisplayNameScript;
                var displayName = window.evaluateContentDisplayNameScript(fn, contentData);

                displayNameField.setValue(displayName);

                this.evaluateDisplayName = true;
            }
        }
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
        return {
            displayName: this.down('#displayName').getValue(),
            contentName: this.down('#contentPath').el.down('input').getValue(),
            contentData: this.getWizardPanel().getData()
        };
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
