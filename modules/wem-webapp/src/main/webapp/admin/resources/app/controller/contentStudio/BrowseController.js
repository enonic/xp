Ext.define('Admin.controller.contentStudio.BrowseController', {
    extend: 'Admin.controller.contentStudio.Controller',

    stores: [
        'Admin.store.contentStudio.ContentTypeStore',
        'Admin.store.contentStudio.ContentTypeTreeStore'
    ],

    models: [],

    views: [
        'Admin.view.contentStudio.FilterPanel',
        'Admin.view.contentStudio.TreeGridPanel',
        'Admin.view.contentStudio.ContextMenu',
        'Admin.view.contentStudio.DetailPanel'
    ],

    init: function () {
        Ext.create('widget.contentStudioContextMenu');

        this.control(
            {
                '*[action=newContentType]': {
                    click: function (btn, evt) {
                        this.showNewContentTypePanel();
                    }
                },
                '*[action=editContentType]': {
                    click: function (btn, evt) {
                        this.showEditContentTypePanel();
                    }
                },
                '*[action=viewContentType]': {
                    click: function (btn, evt) {
                        this.showPreviewContentTypePanel();
                    }
                },
                '*[action=deleteContentType]': {
                    click: function (btn, evt) {
                        this.showDeleteContentTypeWindow();
                    }
                },
                'contentTypeTreeGridPanel grid, treepanel': {
                    selectionchange: this.onGridSelectionChange,
                    itemcontextmenu: this.showContextMenu,
                    itemdblclick: function (btn, evt) {
                        this.showPreviewContentTypePanel();
                    }
                },
                '#searchTextField': {
                    change: this.filterStore
                },
                '#showBaseTypesOnlyCheckbox': {
                    change: this.filterStore
                },
                'button[action=editContentType]': {
                    click: Ext.emptyFn
                },
                'button[action=viewContentType]': {
                    click: Ext.emptyFn
                },
                'button[action=reindexContentTypes]': {
                    click: Ext.emptyFn
                },
                'button[action=exportContentTypes]': {
                    click: Ext.emptyFn
                }
            }
        );
    },


    onGridSelectionChange: function (selModel, selected, opts) {
        this.updateDetailPanel(selModel, selected, opts);
        this.enableToolbarButtons(selected.length > 0);
    },


    updateDetailPanel: function (selModel, selected, opts) {
        if (selected.length > 0) {
            this.getDetailPanel().setData(selected);
        }
    },


    enableToolbarButtons: function (enable) {
        var buttons = Ext.ComponentQuery.query('button[action=editContentType], ' +
                                               'button[action=deleteContentType], ' +
                                               'button[action=viewContentType]');

        Ext.Array.each(buttons, function (button) {
            button.setDisabled(!enable);
        });
    },


    filterStore: function () {
        var store = this.getStore('Admin.store.contentStudio.ContentTypeStore');
        var searchTextValue = this.getFilterPanel().getComponent('searchTextField').getValue();
        var baseTypesOnly = this.getFilterPanel().getComponent('showBaseTypesOnlyCheckbox').getValue();
        var treeGridPanel = this.getTreeGridPanel();
        store.clearFilter();
        if (searchTextValue !== '') {
            treeGridPanel.setActiveList('grid');
        } else {
            treeGridPanel.setActiveList('tree');
        }
        this.doFilterStore(store, searchTextValue, baseTypesOnly);
    },


    doFilterStore: function (store, searchText, baseTypesOnly) {
        var regExp = new RegExp('^' + searchText, 'i');

        store.filterBy(function (record) {
            if (baseTypesOnly) {
                return regExp.test(record.raw.name) && record.raw.type === 'system';
            } else {
                return regExp.test(record.raw.name);
            }
        });
    },


    showContextMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContextMenu().showAt(e.getXY());
        return false;
    },


    getContextMenu: function () {
        return Ext.ComponentQuery.query('contentStudioContextMenu')[0];
    }

});