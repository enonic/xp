/**
 * Base controller for userstore
 */
Ext.define('Admin.controller.userstore.Controller', {
    extend: 'Admin.controller.Controller',

    /*      Base controller for the userstore module      */

    stores: [],
    models: [],
    views: [
        'Admin.view.TabPanel',
        'Admin.view.userstore.MainPanel',
        'Admin.view.userstore.BrowseToolbar',
        'Admin.view.userstore.wizard.UserstoreWizardPanel'
    ],


    init: function () {

        this.control({

        });

        this.application.on({
            showDeleteUserstoreWindow: {
                fn: this.showDeleteUserstoreWindow,
                scope: this
            },
            newUserstore: {
                fn: this.createUserstoreTab,
                scope: this

            },
            editUserstore: {
                fn: this.createUserstoreTab,
                scope: this
            },
            viewUserstore: {
                fn: this.viewUserstore,
                scope: this
            },
            closeUserstoreTab: {
                fn: this.closeUserstoreTab,
                scope: this
            }
        });

    },


    /*      Public, should operate with accounts only      */

    showDeleteUserstoreWindow: function (accounts) {
        if (!accounts) {
            accounts = this.getUserstoreGridPanel().getSelection();
        } else {
            accounts = [].concat(accounts);
        }
        if (accounts && accounts.length > 0) {
            this.getDeleteUserstoreWindow().doShow(accounts);
        }
    },

    createUserstoreTab: function (userstore, forceNew) {
        var tabs = this.getTabs();
        if (tabs) {
            if (!forceNew && userstore) {

                var showPanel = this.getMainPanel();

                showPanel.el.mask("Loading...");

                Admin.lib.RemoteService.userstore_get({name: userstore.name},
                    function (response) {
                        showPanel.el.unmask();
                        if (response.success) {
                            tabs.addTab({
                                xtype: 'userstoreWizardPanel',
                                id: 'tab-userstore-' + userstore.name,
                                title: userstore.name,
                                modelData: response
                            });
                        } else {
                            Ext.Msg.alert("Error", (response.error && response.error.message) ? response.error.message : "Unknown error");
                        }
                    });
            } else {
                tabs.addTab({
                    xtype: 'userstoreWizardPanel',
                    title: 'New Userstore'
                });
            }
        }
    },

    closeUserstoreTab: function (button, e, eOpts) {
        var tabs = this.getTabs();
        if (tabs) {
            var tab = button.up('userstoreFormPanel');
            tabs.remove(tab, true);
        }
    },

    viewUserstore: function (userstore) {
        var tabs = this.getTabs();
        if (tabs) {
            var previewTab = tabs.addTab({
                xtype: 'userstorePreviewPanel',
                tbar: {
                    xtype: 'userstorePreviewToolbar'
                },
                title: userstore.name
            });
            previewTab.setData(userstore);
        }
    },


    getDeleteUserstoreWindow: function () {
        var win = Ext.ComponentQuery.query('deleteUserstoreWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteUserstoreWindow');
        }
        return win;
    },

    getUserstoreGridPanel: function () {
        return Ext.ComponentQuery.query('userstoreGrid')[0];
    },

    getMainPanel: function () {
        return Ext.ComponentQuery.query('mainPanel')[0];

    },

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },


    getTabs: function () {
        // returns tabs if executed in the system scope
        var tabs = this.getCmsTabPanel();
        // returns tabs if executed inside the iframe of the system app
        if (tabs === null && window.parent) {
            tabs = window.parent.Ext.getCmp('systemTabPanelID');
        }
        return tabs;
    }


});