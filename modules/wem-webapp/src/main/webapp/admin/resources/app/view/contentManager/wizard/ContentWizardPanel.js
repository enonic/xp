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

    createSteps: function () {
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
        var evaluateFn = this.data && this.data.contentType && this.data.contentType.contentDisplayNameScript;
        var wizardHeader = Ext.create('Admin.view.WizardHeader', {
            xtype: 'wizardHeader',
            nameConfig: {
                readOnly: headerData.isRoot,
                stripCharsRe: /[^a-z0-9\-\/]+/ig,
                vtype: 'path'
            },
            displayNameConfig: {
                autoFocus: !this.evaluateDisplayName || Ext.isEmpty(evaluateFn)
            },
            data: this.data,
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
