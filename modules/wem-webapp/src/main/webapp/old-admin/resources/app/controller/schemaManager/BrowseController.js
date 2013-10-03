Ext.define('Admin.controller.schemaManager.BrowseController', {
    extend: 'Admin.controller.schemaManager.Controller',

    stores: [
        'Admin.store.schemaManager.ContentTypeStore',
        'Admin.store.schemaManager.ContentTypeTreeStore'
    ],

    models: [],

    views: [
        'Admin.view.schemaManager.FilterPanel',
        'Admin.view.schemaManager.TreeGridPanel',
        'Admin.view.schemaManager.ContextMenu',
        'Admin.view.schemaManager.DetailPanel'
    ],

    init: function () {
        Ext.create('widget.schemaManagerContextMenu');

        this.control(
            {
                '*[action=newSchema]': {
                    click: function (btn, evt) {
                        this.showSelectSchemaWindow();
                    }
                },
                '*[action=editSchema]': {
                    click: function (btn, evt) {
                        this.showEditSchemaPanel();

                    }
                },
                '*[action=viewContentType]': {
                    click: function (btn, evt) {
                        this.showPreviewContentTypePanel();
                    }
                },
                '*[action=deleteSchema]': {
                    click: function (btn, evt) {
                        this.showDeleteSchemaWindow();
                    }
                },

                '#searchTextField': {
                    change: this.filterStore
                },
                '#showSchemaOnlyCheckbox': {
                    change: this.filterStore
                }
            }
        );
    },


    filterStore: function () {
        var store = this.getStore('Admin.store.schemaManager.ContentTypeStore');
        var searchTextValue = this.getFilterPanel().getComponent('searchTextField').getValue();
        var schemaOnly = this.getFilterPanel().getComponent('showSchemasOnlyCheckbox').getValue();
        var treeGridPanel = this.getTreeGridPanel();
        store.clearFilter();
        if (searchTextValue !== '') {
            treeGridPanel.setActiveList('grid');
        } else {
            treeGridPanel.setActiveList('tree');
        }
        this.doFilterStore(store, searchTextValue, schemaOnly);
    },


    doFilterStore: function (store, searchText, schemaOnly) {
        var regExp = new RegExp('^' + searchText, 'i');

        store.filterBy(function (record) {
            if (schemaOnly) {
                return regExp.test(record.raw.name) && record.raw.type === 'system';
            } else {
                return regExp.test(record.raw.name);
            }
        });
    }

});