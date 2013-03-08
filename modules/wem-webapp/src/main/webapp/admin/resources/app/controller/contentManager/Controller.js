/**
 * Base controller for content manager
 */
Ext.define('Admin.controller.contentManager.Controller', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for the content manager module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.contentManager.DeleteContentWindow',
        'Admin.view.contentManager.NewContentWindow',
        'Admin.view.contentManager.LiveEditWindow'
    ],

    init: function () {

        this.control({});

        this.application.on({});

        //Live edit window must be created before usage
        // This should probably be somewhere else
        this.getLiveEditWindow();
    },

    generateTabId: function (content, isEdit) {
        return 'tab-' + ( isEdit ? 'edit' : 'preview') + '-content-' + content.get('path');
    },

    viewContent: function (content, callback) {
        var me = this;

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

                var activeTab = tabs.setActiveTab(me.generateTabId(content[i], true));

                if (!activeTab) {
                    var tabItem = {
                        xtype: 'contentDetail',
                        id: me.generateTabId(content[i], false),
                        isLiveMode: me.getContentDetailPanel().isLiveMode,
                        data: content[i],
                        title: content[i].get('displayName')
                    };
                    tabs.addTab(tabItem);
                }
            }
        }
    },

    editContent: function (content, callback) {

        var me = this;

        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        }
        else {
            content = [].concat(content);
        }
        var tabs = this.getCmsTabPanel();

        var createContentTabFn = function (response) {
            if (Ext.isFunction(callback)) {
                callback();
            }
            return {
                xtype: 'contentWizardPanel',
                title: response.content.displayName,
                isLiveMode: me.getContentDetailPanel().isLiveMode,
                data: {
                    contentType: response.contentType,
                    content: response.content
                }
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
                    Admin.lib.RemoteService.contentType_get(getContentTypeCommand, function (rpcResponse) {
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
                    Admin.lib.RemoteService.content_get(getContentCommand, function (rpcResponse) {
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
                id: me.generateTabId(content[i], true),
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
            for (i = 0; i < content.length; i += 1) {
                var data = content[i];
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
                Admin.lib.RemoteService.contentType_get({
                        format: 'JSON',
                        contentType: qualifiedContentType,
                        mixinReferencesToFormItems: true
                    },
                    function (rpcResponse) {
                        if (rpcResponse.success) {

                            //This is stub, logic for new content creation will be added later
                            var createContentTabFn = function (response) {
                                var contentData = {
                                    content: {
                                        iconUrl: response.iconUrl
                                    },
                                    contentType: response.contentType,
                                    // use first selected record as parent for new content
                                    contentParent: treeGridSelection.length > 0 ? treeGridSelection[0].data : undefined
                                };
                                return {
                                    xtype: 'contentWizardPanel',
                                    title: '[New ' + contentData.contentType.displayName + ']',
                                    data: contentData
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

    deleteContent: function (content) {
        if (!content) {
            var showPanel = this.getContentTreeGridPanel();
            content = showPanel.getSelection();
        }
        else {
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
        }
        else {
            content = [].concat(content);
        }

        var selection = content[0];
        if (selection) {
            Admin.MessageBus.showFeedback({
                title: selection.get('name') + ' duplicated into /path/to/content-copy',
                message: 'Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.',
                opts: {}
            });
        }
    },

    updateDetailPanel: function (selected) {
        var detailPanel = this.getContentDetailPanel();
        detailPanel.setData(selected);
    },

    updateToolbarButtons: function (selected) {
        var toolbar = this.getContentBrowseToolbar();
        var newContentButton = toolbar.down('*[action=newContent]');
        newContentButton.setDisabled(Ext.isEmpty(selected) || selected.length !== 1);

        var deleteContentButton = toolbar.down('*[action=deleteContent]');
        var disabled = false;

        for (i = 0; i < selected.length; i++) {
            var deletable = selected[i].get('deletable');
            if (!deletable) {
                disabled = true;
                break;
            }
        }
        deleteContentButton.setDisabled(disabled);
    },

    showToolbarMenu: function (button, event) {
        event.stopEvent();

        var menu = this.getContentManagerToolbarMenu();
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        menu.items.items[0].setText( 'Details Pane ' + ( vertical ? 'Right' : 'Bottom' ) );
        menu.showAt( event.getX(), button.getEl().getY() + button.getEl().getHeight());
    },

    getContentManagerToolbarMenu: function () {
        var menu = Ext.ComponentQuery.query('contentManagerToolbarMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.contentManagerToolbarMenu');
        }
        return menu;
    },

    moveDetailPanel: function () {
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();

        var toHide = contentDetail[vertical ? 0 : 1];
        var toShow = contentDetail[vertical ? 1 : 0];

        toHide.setVisible(false);
        toShow.setVisible(true);

        if (toShow.isLiveMode != toHide.isLiveMode) {
            toShow.toggleLive();
        }

        var showPanel = this.getContentTreeGridPanel();
        var selected = showPanel.getSelection();

        this.updateDetailPanel(selected);
        this.updateToolbarButtons(selected);
    },

    toggleDetailPanel : function () {
        this.getContentDetailPanel().toggleLive();
    },

    /*      Getters     */

    getContentFilter: function () {
        return Ext.ComponentQuery.query('contentFilter')[0];
    },

    getContentShowPanel: function () {
        return Ext.ComponentQuery.query('contentShow')[0];
    },

    getContentBrowseToolbar: function () {
        return this.getContentShowPanel().down('browseToolbar');
    },

    getContentTreeGridPanel: function () {
        return this.getContentShowPanel().down('contentTreeGridPanel');
    },

    getContentDetailPanel: function () {
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        return Ext.ComponentQuery.query('contentDetail')[vertical ? 0 : 1];
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
    },

    getNewContentWindow: function () {
        var win = Ext.ComponentQuery.query('newContentWindow')[0];
        if (!win) {
            win = Ext.create('widget.newContentWindow');
        }
        return win;
    },

    getLiveEditWindow: function () {
        var win = Ext.ComponentQuery.query('liveEditWindow')[0];
        if (!win) {
            win = Ext.create('widget.liveEditWindow');
        }
        return win;
    }


});
