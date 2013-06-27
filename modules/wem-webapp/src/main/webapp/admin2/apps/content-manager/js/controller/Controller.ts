/**
 * Base controller for content manager
 */
Ext.define('Admin.controller.Controller', {
    extend: 'Admin.controller.BaseController',

    /*      Base controller for the content manager module      */

    stores: [
        'Admin.store.contentManager.ContentStore',
        'Admin.store.contentManager.ContentTreeStore'
    ],
    models: [
        'Admin.model.contentManager.ContentModel'
    ],

    init: function () {

        app_browse.NewContentEvent.on((event) => {
            this.getNewContentWindow().doShow();
        });

        app_browse.OpenContentEvent.on((event) => {
            this.viewContent(event.getModels());
        });

        app_browse.EditContentEvent.on((event) => {
            this.editContent(event.getModels());
        });

        app_browse.DeleteContentEvent.on((event) => {
            components.contentDeleteDialog.setContentToDelete(event.getModels());
            components.contentDeleteDialog.open();
        });

        app_browse.DuplicateContentEvent.on((event) => {
            this.duplicateContent(event.getModels());
        });

        app_browse.MoveContentEvent.on((event) => {
            console.log('TODO: implement move content');
        });

        app_browse.ShowPreviewEvent.on((event) => {
            console.log('TODO: implement show preview');
        });

        app_browse.ShowDetailsEvent.on((event) => {
            console.log('TODO: implement show details');
        });

        app_browse.GridSelectionChangeEvent.on((event) => {
            this.updateDetailPanel(event.getModels());
        });

    },

    getNewContentWindow: function () {
        var win = Ext.ComponentQuery.query('newContentWindow')[0];
        if (!win) {
            win = <any> Ext.create('widget.newContentWindow');
        }
        return win;
    },

    generateTabId: function (content, isEdit) {
        return 'tab-' + ( isEdit ? 'edit' : 'preview') + '-content-' + content.get('path');
    },

    viewContent: function (contentModels, callback?, contentOpenedFromLiveEdit?) {
        var me = this,
            tabs = this.getCmsTabPanel();

        if (tabs) {
            for (var i = 0; i < contentModels.length; i += 1) {

                var activeTab = tabs.setActiveTab(me.generateTabId(contentModels[i], true));

                if (!activeTab) {
                    var tabItem = {
                        xtype: 'contentDetail',
                        id: me.generateTabId(contentModels[i], false),

                        /* For 18/4 */
                        isLiveMode: contentOpenedFromLiveEdit || me.getContentDetailPanel().isLiveMode,

                        data: contentModels[i],
                        title: contentModels[i].get('displayName'),
                        isFullPage: true
                    };
                    tabs.addTab(tabItem);
                }
            }
        }
    },

    editContent: function (contentModel, callback) {
        var me = this,
            tabs = this.getCmsTabPanel();

        var createContentTabFn = function (response) {
            if (Ext.isFunction(callback)) {
                callback();
            }
            return {
                xtype: 'contentLiveEditPanel',
                title: response.content.displayName,
                isLiveMode: me.getContentDetailPanel().isLiveMode,
                contentType: response.contentType,
                content: response.content,
                data: {}
            };
        };

        var openEditContentTabFn = function (selectedContent) {
            var requestConfig = {
                doTabRequest: function (handleRpcResponse) {
                    var getContentTypeResponse, getContentResponse;

                    var getContentTypeCommand = {
                        format: 'JSON',
                        contentType: selectedContent.get('type'),
                        mixinReferencesToFormItems: true
                    };
                    api_remote.RemoteService.contentType_get(getContentTypeCommand, function (rpcResponse) {
                        getContentTypeResponse = rpcResponse;
                        if (getContentTypeResponse && getContentTypeResponse.success && getContentResponse && getContentResponse.success) {
                            // both responses received, combine responses and pass them to callback
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });

                    var getContentCommand = {
                        path: selectedContent.get('path')
                    };
                    api_remote.RemoteService.content_get(getContentCommand, function (rpcResponse) {
                        getContentResponse = rpcResponse;
                        if (getContentResponse && getContentResponse.success && getContentTypeResponse && getContentTypeResponse.success) {
                            // both responses received, combine responses and pass them to callback
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });
                },
                createTabFromResponse: createContentTabFn
            };
            var tabItem = {
                id: me.generateTabId(contentModel[i], true),
                title: selectedContent.get('displayName'),
                data: selectedContent,
                closable: true,
                editing: true,
                layout: 'fit'
            };

            //check if preview tab is open and close it
            var index = tabs.items.indexOfKey(me.generateTabId(selectedContent, false));
            if (index >= 0) {
                tabs.remove(index);
            }

            tabs.addTab(tabItem, index >= 0 ? index : undefined, requestConfig);
        };

        var i;
        if (tabs) {
            for (i = 0; i < contentModel.length; i += 1) {
                var data = contentModel[i];
                //TODO: implement when content specification will be developed
                openEditContentTabFn(data);
            }
        }
    },

    createContent: function (type, qualifiedContentType, contentTypeName) {
        var tabs = this.getCmsTabPanel();
        if (tabs) {
            var tab;
            var treeGridSelection = this.getContentTreeGridPanel().getSelection();

            switch (type) {
            case 'contentType':
                api_remote.RemoteService.contentType_get({
                        format: 'JSON',
                        contentType: qualifiedContentType,
                        mixinReferencesToFormItems: true
                    },
                    function (rpcResponse) {
                        if (rpcResponse.success) {

                            //This is stub, logic for new content creation will be added later
                            var createContentTabFn = function (response) {
                                return {
                                    xtype: 'contentLiveEditPanel',
                                    title: '[New ' + response.contentType.displayName + ']',
                                    content: {
                                        iconUrl: response.iconUrl
                                    },
                                    contentType: response.contentType,
                                    contentParent: treeGridSelection.length > 0 ? treeGridSelection[0].data : undefined,
                                    data: {}
                                };
                            };


                            var requestConfig = {
                                doTabRequest: function (handleRpcResponse) {
                                    handleRpcResponse(rpcResponse);
                                },
                                createTabFromResponse: createContentTabFn
                            };

                            var tabItem = {
                                id: 'tab-new-content-' + qualifiedContentType,
                                data: {
                                    name: contentTypeName,
                                    content: {
                                        iconUrl: rpcResponse.iconUrl
                                    }
                                },
                                title: '[New ' + contentTypeName + ']',
                                closable: true,
                                editing: true,
                                layout: 'fit'
                            };
                            tabs.addTab(tabItem, undefined, requestConfig);

                        }
                    });
                break;
            case 'site':
                tab = {
                    xtype: 'panel',
                    html: 'New site wizard here',
                    title: 'New Site'
                };
                tabs.addTab(tab);
                break;
            }

        }
    },

    duplicateContent: function (content) {
        // TODO: implement duplicate content
        var selection = content[0];
        if (selection) {
            api_notify.showFeedback(selection.get('name') + ' duplicated into /path/to/content-copy');
        }
    },

    updateDetailPanel: function (selected) {

        // need to use this methods to preserve persistent selection
        var selection = this.getContentTreeGridPanel().getSelection();
        this.getContentDetailPanel().update(selection);
    },

    updateToolbarButtons: function (selected) {
        var toolbar = this.getContentBrowseToolbar();
        if (toolbar) {
            var detailPanel = this.getContentDetailPanel();
            var newContentButton = toolbar.down('*[action=newContent]');
            newContentButton.setDisabled(Ext.isEmpty(selected) || selected.length !== 1 || (!selected[0].get('allowsChildren')));

            var deleteContentButton = toolbar.down('*[action=deleteContent]');
            var disabled = false;

            var i;
            for (i = 0; i < selected.length; i++) {
                var deletable = selected[i].get('deletable');
                if (!deletable) {
                    disabled = true;
                    break;
                }
            }
            deleteContentButton.setDisabled(disabled);
        }
    },

    /**
     * @param values to use for load, pass {} - to reset, pass undefined - to reload
     */
    loadContentAndFacets: function (values) {

        var me = this,
            filter = this.getContentFilter(),
            params = this.createLoadContentParams(values || filter.getValues());

        api_remote.RemoteService.content_find(params, function (response) {
            if (response && response.success) {

                // set facet data
                me.getContentFilter().updateFacets(response.facets);

                var ids = Ext.Array.pluck(response.contents, 'id'),
                    treeGridPanel = me.getContentTreeGridPanel(),
                    filterDirty = filter.isDirty();

                // show/hide result count in toolbar
                treeGridPanel.setResultCountVisible(filterDirty);

                // set tree data
                if (!filterDirty) {
                    // show everything even if we have ids returned because no filter set
                    treeGridPanel.setRemoteSearchParams({});
                    treeGridPanel.refresh();
                } else {
                    if (ids.length > 0) {
                        // show  ids
                        treeGridPanel.setRemoteSearchParams({ contentIds: ids });
                        treeGridPanel.refresh();
                    } else {
                        // show none
                        treeGridPanel.removeAll();
                        treeGridPanel.updateResultCount(0);
                    }
                }
            }
        })

    },

    /* For 18/4 */

    getDimensionsForDevice: function (device) {
        var dimensions;

        switch (device) {
        case 'DESKTOP':
            dimensions = {width: '100%', height: '100%'};
            break;
        case 'IPHONE_5_VERTICAL':
            dimensions = {width: '320px', height: '568px'};
            break;
        case 'IPHONE_5_HORIZONTAL':
            dimensions = {width: '568px', height: '320px'};
            break;
        case 'IPAD_3_VERTICAL':
            dimensions = {width: '768px', height: '1024px'};
            break;
        case 'IPAD_3_HORIZONTAL':
            dimensions = {width: '1024px', height: '768px'};
            break;
        default:
            dimensions = {width: '100%', height: '100%'};
        }
        return dimensions;
    },

    /**
     * @private
     */
    createLoadContentParams: function (values) {

        var now = new Date();
        var oneDayAgo = new Date();
        var oneWeekAgo = new Date();
        var oneHourAgo = new Date();
        oneDayAgo.setDate(now.getDate() - 1);
        oneWeekAgo.setDate(now.getDate() - 7);
        Admin.lib.DateHelper.addHours(oneHourAgo, -1);

        var facets = {
            "space": {
                "terms": {
                    "field": "space",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "contentType": {
                "terms": {
                    "field": "contentType",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "< 1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            }
        };

        var ranges = [];
        if (values.ranges) {
            for (var i = 0; i < values.ranges.length; i++) {
                var lower;
                switch (values.ranges[i]) {
                case '< 1 day':
                    lower = oneDayAgo;
                    break;
                case '< 1 hour':
                    lower = oneHourAgo;
                    break;
                case '< 1 week':
                    lower = oneWeekAgo;
                    break;
                default:
                    lower = null;
                    break;
                }
                ranges.push({
                    lower: lower,
                    upper: null
                })
            }
        }

        return {
            fulltext: values.query || '',
            contentTypes: values.contentType || [],
            spaces: values.space || [],
            ranges: ranges || [],
            facets: facets || {},
            include: true
        };
    },


    /*      Getters     */

    getContentFilter: function () {
        return Ext.ComponentQuery.query('contentFilter')[0];
    },

    getContentManagerContextMenu: function ():app_browse.ContentTreeGridContextMenu {
        if (!components.contextMenu) {
            components.contextMenu = new app_browse.ContentTreeGridContextMenu();
        }

        return components.contextMenu;
    },

    getContentTreeGridPanel: function () {
        return components.gridPanel;
    },

    getContentDetailPanel: function ():app_browse.ContentDetailPanel {
        return components.detailPanelHorizontal.isVisible() ? components.detailPanelHorizontal : components.detailPanelVertical;
    },

    getPersistentGridSelectionPlugin: function () {
        return this.getContentGridPanel().getPlugin('persistentGridSelection');
    },

    getLiveEditTestWindow: function () {
        var win = Ext.ComponentQuery.query('liveEditTestWindow')[0];
        if (!win) {
            win = <any> Ext.create('widget.liveEditTestWindow');
        }
        return win;
    }

});
