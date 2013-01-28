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
                '*[action=newMixin]': {
                    click: function (btn, evt) {
                        this.showNewMixinPanel();
                    }
                },
                '*[action=newRelationshipType]': {
                    click: function (btn, evt) {
                        this.showNewRelationshipTypePanel();
                    }
                },
                '*[action=editBaseType]': {
                    click: function (btn, evt) {
                        this.showEditBaseTypePanel();

                    }
                },
                '*[action=viewContentType]': {
                    click: function (btn, evt) {
                        this.showPreviewContentTypePanel();
                    }
                },
                '*[action=deleteBaseType]': {
                    click: function (btn, evt) {
                        this.showDeleteContentTypeWindow();
                    }
                },

                '#searchTextField': {
                    change: this.filterStore
                },
                '#showBaseTypesOnlyCheckbox': {
                    change: this.filterStore
                }
            }
        );
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
    }

});