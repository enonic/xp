Ext.define('Admin.view.account.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accountDetail',
    split: true,
    autoScroll: true,
    layout: 'card',
    collapsible: true,

    initComponent: function () {
        var largeBoxesPanel = this.createLargeBoxSelection();
        var userPreviewPanel = this.createUserPreviewPanel();
        var groupPreviewPanel = this.createGroupPreviewPanel();
        var smallBoxesPanel = this.createSmallBoxSelection();
        var noneSelectedPanel = this.createNoneSelection();

        this.items = [noneSelectedPanel, userPreviewPanel, groupPreviewPanel,
            largeBoxesPanel, smallBoxesPanel];
        this.callParent(arguments);
    },

    showAccountPreview: function (data) {
        if (data) {
            var activeTab;
            switch (data.type) {
            case 'user':
                activeTab = this.down('userPreviewPanel');
                break;
            case 'role':
            case 'group':
                activeTab = this.down('groupPreviewPanel');
                break;
            }
            if (activeTab) {
                activeTab.setData(data);
                this.getLayout().setActiveItem(activeTab);
            }
        }
    },


    showMultipleSelection: function (data, detailed) {
        var activeItem;
        if (detailed) {
            activeItem = this.down('#largeBoxPanel');
            this.getLayout().setActiveItem('largeBoxPanel');
        } else {
            activeItem = this.down('#smallBoxPanel');
            this.getLayout().setActiveItem('smallBoxPanel');
        }

        activeItem.update({users: data});
    },

    showNoneSelection: function (data) {
        var activeItem = this.down('#noneSelectedPanel');
        this.getLayout().setActiveItem('noneSelectedPanel');
        activeItem.update(data);
    },

    createUserPreviewPanel: function () {
        return {
            xtype: 'userPreviewPanel',
            showToolbar: false
        };
    },

    createGroupPreviewPanel: function () {
        return {
            xtype: 'groupPreviewPanel',
            showToolbar: false
        };
    },

    createNoneSelection: function () {
        var tpl = new Ext.XTemplate(Templates.account.noUserSelected);

        var panel = {
            xtype: 'panel',
            itemId: 'noneSelectedPanel',
            styleHtmlContent: true,
            padding: 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    createLargeBoxSelection: function () {
        var tpl = Ext.Template(Templates.account.selectedUserLarge);

        var panel = {
            xtype: 'panel',
            itemId: 'largeBoxPanel',
            styleHtmlContent: true,
            autoScroll: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            padding: 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    createSmallBoxSelection: function () {
        var tpl = Ext.Template(Templates.account.selectedUserSmall);

        var panel = {
            xtype: 'panel',
            itemId: 'smallBoxPanel',
            styleHtmlContent: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            autoScroll: true,
            padding: 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    deselectItem: function (event, target) {
        var className = target.className;
        if (className && className === 'remove-selection') {
            var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button-')[1];
            var userGridSelModel = this.up('cmsTabPanel').down('accountGrid').getSelectionModel();
            var persistentGridSelection = this.persistentGridSelection;
            var selection = persistentGridSelection.getSelection();
            Ext.each(selection, function (item) {
                if (item.get('key') === key) {
                    Ext.get('selected-item-box-' + key).remove();
                    persistentGridSelection.deselect(item);
                }
            });
        }
    },

    setCurrentAccount: function (user) {
        this.currentAccount = user;
    },

    getCurrentAccount: function () {
        return this.currentAccount;
    },

    updateTitle: function (persistentGridSelection) {
        this.persistentGridSelection = persistentGridSelection;
        var count = persistentGridSelection.getSelectionCount();
        var header = count + " Account(s) selected";
        if (count > 0) {
            header += " (<a href='javascript:;' class='clearSelection'>Clear selection</a>)";
        }
        this.setTitle(header);

        var clearSel = this.header.el.down('a.clearSelection');
        if (clearSel) {
            clearSel.on("click", function () {
                persistentGridSelection.clearSelection();
            }, this);
        }

    }

});
