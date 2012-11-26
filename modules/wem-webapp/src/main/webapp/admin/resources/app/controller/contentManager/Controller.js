/**
 * Base controller for content manager
 */
Ext.define('Admin.controller.contentManager.Controller', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DeleteContentWindow'
    ],


    init: function () {

        this.control({});

        this.application.on({});

    },

    viewContent: function (content, callback) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }

        var tabs = this.getCmsTabPanel();
        var i;
        if (tabs) {
            for (i = 0; i < content.length; i += 1) {
                tabs.addTab({
                    xtype: 'contentDetail',
                    data: content[i],
                    title: 'View Content'
                });
            }
        }
    },

    editContent: function (content, callback) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }
        var tabs = this.getCmsTabPanel();
        var createContentTabFn = function (response) {
            if (Ext.isFunction(callback)) {
                callback();
            }
            return {
                xtype: 'contentWizardPanel',
                title: 'New Content',
                data: {contentType: response.contentType, content: response.content}
            };
        };
        var createSiteTabFn = function (response) {
            if (Ext.isFunction(callback)) {
                callback();
            }
            return {
                xtype: 'panel',
                title: 'New Site',
                html: 'Site wizard will be here',
                data: response
            };
        };
        var openEditContentTabFn = function (selectedContent) {
            var requestConfig = {
                doTabRequest: function (handleRpcResponse) {
                    var getContentTypeResponse, getContentResponse;
                    // request content type and content to be edited, in parallel
                    Admin.lib.RemoteService.contentType_get({contentType: selectedContent.get('type')}, function (rpcResponse) {
                        getContentTypeResponse = rpcResponse;
                        if (getContentTypeResponse && getContentTypeResponse.success && getContentResponse && getContentResponse.success) {
                            // both responses received, combine responses and pass them to callback
                            getContentTypeResponse.content = getContentResponse.content;
                            handleRpcResponse(getContentTypeResponse);
                        }
                    });
                    Admin.lib.RemoteService.content_get({path: selectedContent.get('path')}, function (rpcResponse) {
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
                itemId: 'edit-content-tab-' + selectedContent.get('path'),
                title: selectedContent.get('displayName'),
                closable: true,
                layout: 'fit'
            };
            tabs.addTab(tabItem, undefined, requestConfig);
        };
        var openEditSiteTabFn = function (selectedContent) {
            var requestConfig = {
                doTabRequest: function (handleRpcResponse) {
                    // data call here
                },
                createTabFromResponse: createSiteTabFn
            };
            var tabItem = {
                itemId: 'edit-site-tab-' + selectedContent.get('path'),
                title: selectedContent.get('displayName'),
                closable: true,
                layout: 'fit'
            };
            tabs.addTab(tabItem, undefined, requestConfig);
        };

        var i;
        if (tabs) {
            var tab;
            for (i = 0; i < content.length; i += 1) {
                var data = content[i];
                //TODO: implement when content specification will be developed
                switch (data.get('type')) {
                case 'myModule:myType':
                case 'News:Article':
                case 'News:Article2':
                    openEditContentTabFn(data);
                    break;
                case 'myModule:mySite':
                    openEditSiteTabFn(data);
                    break;
                }
            }
        }
    },

    createContent: function (type, qualifiedContentType) {
        var tabs = this.getCmsTabPanel();
        if (tabs) {
            var tab;
            var treeGridSelection = this.getContentTreeGridPanel().getSelection();

            switch (type) {
            case 'contentType':
                //This is stub, logic for new content creation will be added later
                var createContentTabFn = function (response) {
                    var contentData = {
                        contentType: response.contentType,
                        // use first selected record as parent for new content
                        contentParent: treeGridSelection.length > 0 ? treeGridSelection[0].data : undefined
                    };
                    return {
                        xtype: 'contentWizardPanel',
                        title: 'New Content',
                        data: contentData
                    };
                };
                var requestConfig = {
                    doTabRequest: function (handleRpcResponse) {
                        Admin.lib.RemoteService.contentType_get({contentType: qualifiedContentType}, function (rpcResponse) {
                            if (rpcResponse.success) {
                                handleRpcResponse(rpcResponse);
                            }
                        });
                    },
                    createTabFromResponse: createContentTabFn
                };
                var tabItem = {
                    itemId: 'new-content-tab',
                    title: 'New Content',
                    closable: true,
                    layout: 'fit'
                };
                tabs.addTab(tabItem, undefined, requestConfig);

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

    deleteContent: function (content) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }

        if (content && content.length > 0) {
            this.getDeleteContentWindow().doShow(content);
        }
    },

    duplicateContent: function (content) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        } else {
            content = [].concat(content);
        }

        var selection = content[0];
        var parentApp = parent.mainApp;
        if (parentApp && selection) {
            parentApp.fireEvent('notifier.show', selection.get('name') + ' duplicated into /path/to/content-copy',
                'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                true);
        }
    },

    /*      Getters     */

    getContentFilter: function () {
        return Ext.ComponentQuery.query('contentFilter')[0];
    },

    getContentShowPanel: function () {
        return Ext.ComponentQuery.query('contentShow')[0];
    },

    getContentTreeGridPanel: function () {
        return this.getContentShowPanel().down('contentTreeGridPanel');
    },

    getContentDetailPanel: function () {
        return Ext.ComponentQuery.query('contentDetail')[0];
    },

    getPersistentGridSelectionPlugin: function () {
        return this.getContentGridPanel().getPlugin('persistentGridSelection');
    },

    getDeleteContentWindow: function () {
        var win = Ext.ComponentQuery.query('deleteContentWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteContentWindow');
        }
        return win;
    }


});
