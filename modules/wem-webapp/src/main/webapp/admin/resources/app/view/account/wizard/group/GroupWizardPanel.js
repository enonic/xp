Ext.define('Admin.view.account.wizard.group.GroupWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupWizardPanel',

    requires: [
        'Admin.view.WizardPanel',
        'Admin.view.account.wizard.group.GroupWizardToolbar',
        'Admin.view.account.wizard.group.WizardStepGeneralPanel',
        'Admin.view.account.wizard.group.WizardStepMembersPanel',
        'Admin.view.SummaryTreePanel'
    ],

    layout: 'column',

    border: 0,
    autoScroll: true,

    defaults: {
        border: false
    },

    headerTemplate: '<div class="admin-wizard-header">' +
                    '<h1 class="admin-display-name admin-edited-field">{displayName}</h1>' +
                    '<span>{qualifiedName}</span>' +
                    '</div>',

    initComponent: function () {
        var me = this;
        var isNew = this.isNewGroup();
        var isRole = this.isRole();
        var steps = me.getSteps();

        var headerData = me.resolveHeaderData(this.data);

        me.tbar = Ext.createByAlias('widget.groupWizardToolbar', {
            xtype: 'groupWizardToolbar',
            isNew: isNew,
            isRole: isRole
        });

        me.items = [
            {
                xtype: 'container',
                padding: 5,
                border: false,
                items: [
                    {
                        xtype: 'image',
                        plain: true,
                        width: 100,
                        height: 100,
                        src: headerData.iconUrl,
                        listeners: {
                            render: function (cmp) {
                                Ext.tip.QuickTipManager.register({
                                    target: cmp.el,
                                    text: headerData.tipText,
                                    width: 100,
                                    dismissDelay: 10000 // Hide after 10 seconds hover
                                });
                            }
                        }
                    }
                ]
            },
            {
                xtype: 'container',
                columnWidth: 1,
                padding: '10 10 10 0',
                defaults: {
                    border: false
                },
                items: [
                    {
                        xtype: 'component',
                        itemId: 'wizardHeader',
                        autoHeight: true,
                        cls: 'admin-wizard-header-container',
                        border: false,
                        tpl: new Ext.XTemplate(me.headerTemplate),
                        data: headerData
                    },
                    {
                        xtype: 'wizardPanel',
                        showControls: true,
                        isNew: isNew,
                        items: steps
                    }
                ]
            }
        ];

        this.callParent(arguments);

        this.on('afterrender', function (groupWizard) {
            me.removeEmptySteps(groupWizard.getWizardPanel());
        });

        if (me.data) {
            var wizard = me.down('wizardPanel');
            wizard.addData({userStore: me.data.userStore});
            wizard.addData({key: me.data.key});
            wizard.addData({'displayName': me.data.displayName});
            wizard.addData({builtIn: me.data.type === 'role'});
        }

    },

    getSteps: function () {
        var me = this;
        var isRole = me.isRole();

        var generalStep = {
            stepTitle: "General",
            data: this.data,
            xtype: 'wizardStepGeneralPanel'
        };
        var membersStep = {
            stepTitle: "Members",
            data: this.data,
            xtype: 'wizardStepMembersPanel'
        };
        var summaryStep = {
            stepTitle: 'Summary',
            dataType: 'group',
            xtype: 'summaryTreePanel'
        };

        if (isRole) {
            return [membersStep, summaryStep];
        } else {
            return [generalStep, membersStep, summaryStep];
        }
    },

    resolveHeaderData: function (data) {
        var isNew = this.isNewGroup();
        return {
            displayName: isNew ? 'Display name' : data.displayName,
            qualifiedName: isNew ? this.userstore + '\\' : data.qualifiedName,
            iconUrl: isNew ? 'rest/account/image/default/group' : data.image_url,
            tipText: isNew ? 'Group' : Ext.String.capitalize(data.type)
        }
    },

    removeEmptySteps: function (wizardPanel) {
        wizardPanel.items.each(function (item) {
            if (!item.alwaysKeep && item.getForm && (item.getForm().getFields().getCount() === 0)) {
                wizardPanel.remove(item);
            }
        });
    },

    isNewGroup: function () {
        return Ext.isEmpty(this.data);
    },

    isRole: function () {
        return this.data && this.data.type === 'role';
    },

    getWizardPanel: function () {
        return this.down('wizardPanel');
    },

    getData: function () {
        return this.getWizardPanel().getData();
    }

});
