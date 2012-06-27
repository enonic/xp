Ext.define('Admin.controller.datadesigner.BrowseController', {
    extend: 'Admin.controller.datadesigner.Controller',

    stores: [
        'Admin.store.datadesigner.ContentTypeStore'
    ],

    models: [],

    views: [
        'Admin.view.datadesigner.FilterPanel',
        'Admin.view.datadesigner.GridPanel',
        'Admin.view.datadesigner.ContextMenu',
        'Admin.view.datadesigner.DetailPanel'
    ],

    init: function () {
        Ext.create('widget.datadesignerContextMenu');

        this.control(
            {
                '*[action=editContentType]': {
                    click: this.showEditContentTypePanel
                },
                '*[action=viewContentType]': {
                    click: this.showPreviewContentTypePanel
                },
                '*[action=deleteContentType]': {
                    click: this.showDeleteContentTypeWindow
                },
                'contentTypeGridPanel': {
                    selectionchange: this.onGridSelectionChange,
                    itemcontextmenu: this.showContextMenu,
                    itemdblclick: this.showPreviewContentTypePanel
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
            var panel = this.getDetailPanel();
            var cardLayout = panel.getLayout();
            panel.setData(selected[0].raw);
            cardLayout.setActiveItem('previewContainer');
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
        var store = this.getStore('Admin.store.datadesigner.ContentTypeStore');
        var searchTextValue = this.getFilterPanel().getComponent('searchTextField').getValue();
        var baseTypesOnly = this.getFilterPanel().getComponent('showBaseTypesOnlyCheckbox').getValue();
        store.clearFilter();

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
        return Ext.ComponentQuery.query('datadesignerContextMenu')[0];
    }

});