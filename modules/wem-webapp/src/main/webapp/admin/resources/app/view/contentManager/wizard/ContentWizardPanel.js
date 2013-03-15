Ext.define('Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentWizardPanel',

    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.WizardHeader',
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

        this.evaluateDisplayName = this.isNewContent();

        var evaluateFn = this.data && this.data.contentType && this.data.contentType.contentDisplayNameScript;

        var headerData = this.prepareHeaderData(this.data);

        me.tbar = Ext.createByAlias('widget.contentWizardToolbar', {
            isLiveMode: me.isLiveMode
        });

        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            nameConfig: {
                readOnly: headerData.isRoot,
                stripCharsRe: /[^a-z0-9\-\/]+/ig,
                vtype: 'path'
            },
            displayNameConfig: {
                autoFocus: !me.evaluateDisplayName || Ext.isEmpty(evaluateFn)
            },
            data: me.data,
            prepareHeaderData: me.prepareHeaderData
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
                    style: 'overflow: visible',
                    bodyStyle: 'overflow: visible',
                    defaults: {
                        border: false
                    },
                    items: [
                        wizardHeader,
                        {
                            xtype: 'wizardPanel',
                            validateItems: [wizardHeader],
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
            path: contentPath,
            name: contentName,
            isRoot: isRoot
        };
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

    isNewContent: function () {
        return !this.data || !this.data.content || Ext.isEmpty(this.data.content.path);
    },

    getWizardHeader: function () {
        return this.down('wizardHeader');
    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        var data = {
            contentData: this.getWizardPanel().getData()
        };
        Ext.apply(data, this.getWizardHeader().getData());
        return data;
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
