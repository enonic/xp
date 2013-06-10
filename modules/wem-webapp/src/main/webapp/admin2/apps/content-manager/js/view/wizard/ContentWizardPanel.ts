Ext.define('Admin.view.contentManager.wizard.ContentWizardPanel', {
    extend: 'Admin.view.WizardPanel',
    alias: 'widget.contentWizardPanel',

    requires: [
        'Admin.view.WizardHeader',
        'Admin.view.contentManager.wizard.ContentWizardToolbar',
        'Admin.view.contentManager.wizard.ContentDataPanel'
    ],

    header: false,

    border: 0,
    autoScroll: false,
    evaluateDisplayName: true,
    contentNameOverridden: false,

    defaults: {
        border: false
    },

    listeners: {
        copyremoved: function (copy) {
            var me = this;
            var data = this.getData();
            var copyData = copy.getValue();
            if (copyData instanceof Array) {
                Ext.each(copyData, function (copyDataItem) {
                    me.deleteData(copyDataItem.path);
                });
            } else {
                this.deleteData(copyData.path);
            }
        }
    },

    initComponent: function () {
        var me = this;

        this.evaluateDisplayName = this.isNewContent();

        this.callParent(arguments);

    },

    prepareHeaderData: function (data) {
        var contentPath = '';
        var isRoot = false;
        var isNew = true;

        if (this.content) {
            if (!Ext.isEmpty(this.content.path)) {
                contentPath = this.content.path;
                isNew = false;
            }
            if (Ext.isDefined(this.content.isRoot)) {
                isRoot = this.content.isRoot;
            }
        }
        if (isNew && this.contentParent) {
            if (!Ext.isEmpty(this.contentParent.path)) {
                // content is deletable if not root
                var isParentRoot = !this.contentParent.deletable || false;
                contentPath = this.contentParent.path + (isParentRoot ? '' : '/');
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
            imageUrl: this.content ? this.content.iconUrl : undefined,
            displayName: this.content ? this.content.displayName : undefined,
            path: contentPath,
            name: isNew ? undefined : contentName,
            isRoot: isRoot,
            isNew: isNew
        };
    },

    createSteps: function () {
        var dataStep = {
            stepTitle: this.contentType ? this.contentType.displayName : "Data",
            xtype: 'contentDataPanel',
            contentType: this.contentType,
            content: this.content
        };
        var metaStep = {
            stepTitle: 'Meta',
            xtype: 'panel'
        };
        var pageStep = {
            stepTitle: 'Page',
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

        return <any[]>[dataStep, metaStep, pageStep, securityStep, summaryStep];
    },

    isNewContent: function () {
        return !this.data || !this.content || Ext.isEmpty(this.content.path);
    },

    washDirtyForms: function () {
        for (var i = this.dirtyItems.length - 1; i >= 0; i--) { // dirtyForms
            this.washDirtyForm(this.dirtyItems[i]);
        }
        this.dirtyItems = [];
        this.isWizardDirty = false;
    },

    washDirtyForm: function (dirtyForm) {
        if (dirtyForm.isDirty()) {
            dirtyForm.getFields().each(function (me) {
                me.originalValue = me.getValue();
                me.checkDirty();
            });
        }
    },

    createWizardHeader: function () {
        var headerData = this.prepareHeaderData(this.data);
        var evaluateFn = this.data && this.contentType && this.contentType.contentDisplayNameScript;
        var wizardHeader = <any> Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            nameConfig: {
                readOnly: headerData.isRoot,
                stripCharsRe: /[^a-z0-9\-_]+/ig,
                vtype: 'path'
            },
            displayNameConfig: {
                emptyText: headerData.isNew ? 'New Content' : 'Display Name',
                autoFocus: headerData.isNew && Ext.isEmpty(evaluateFn)
            },
            data: this.data,
            content: this.content,
            prepareHeaderData: this.prepareHeaderData
        });
        this.validateItems.push(wizardHeader);
        return wizardHeader;
    },

    createActionButton: function () {
        return {
            xtype: 'button',
            text: 'Publish',
            action: 'publishContent'
        };
    },

    createIcon: function () {
        var me = this;
        var headerData = this.prepareHeaderData(this.data);
        return {
            xtype: 'image',
            width: 110,
            height: 110,
            src: headerData.imageUrl,
            listeners: {
                render: function (cmp) {

                    var contentType = (me.data && me.contentType) ? me.contentType : undefined;
                    if (contentType) {
                        var toolText = '<strong>' + contentType.displayName + '</strong></br>' +
                                       contentType.module + ':' + contentType.name;

                        var tip = <any> Ext.create('Ext.tip.ToolTip', {
                            target: cmp.el,
                            html: toolText,
                            padding: 10,
                            styleHtmlContent: true,
                            dismissDelay: 10000 // Hide after 10 seconds hover
                        });
                    }
                }
            }
        };
    },

    getWizardHeader: function () {
        return this.down('wizardHeader');
    },

    setLiveMode: function (mode) {
        this.getLayout().setActiveItem(mode ? 1 : 0);

        if (mode) {
            var livePreview = this.down('#livePreview');
            //TODO update urls when they are ready
            livePreview.load('/dev/live-edit/page/bootstrap.jsp', true);
        }
    },

    getData: function () {
        var data = {
            contentData: this.callParent()
        };
        Ext.apply(data, this.getWizardHeader().getData());
        return data;
    }

});
