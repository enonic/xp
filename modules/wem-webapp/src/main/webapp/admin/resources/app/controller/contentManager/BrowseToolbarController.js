Ext.define('Admin.controller.contentManager.BrowseToolbarController', {
    extend: 'Admin.controller.contentManager.Controller',

    /*      Controller for handling Toolbar UI events       */

    stores: [
    ],

    models: [
    ],
    views: [
        'Admin.view.FileUploadWindow',
        'Admin.view.contentManager.NewContentWindow'
    ],

    requires: [
        'Admin.view.contentManager.ToolbarMenu'
    ],

    init: function () {

        this.control({
            'browseToolbar *[action=showToolbarMenu]': {
                click: function (button, event) {
                    this.showToolbarMenu(button, event);
                }
            },
            'contentManagerToolbarMenu *[action=moveDetailPanel]': {
                click: function (button, event) {
                    this.moveDetailPanel(button, event);
                }
            },
            'browseToolbar *[action=toggleLive]': {
                change: function (slider, state) {
                    this.getContentDetailPanel().toggleLive();
                }
            }
        });
    },


    showToolbarMenu: function (button, event) {
        event.stopEvent();

        var menu = this.getContentManagerToolbarMenu();
        var contentDetail = Ext.ComponentQuery.query('contentDetail');
        var vertical = contentDetail[0].isVisible();
        menu.items.items[0].setText('Details Pane ' + ( vertical ? 'Right' : 'Bottom' ));
        menu.showAt(event.getX(), button.getEl().getY() + button.getEl().getHeight());
    },

    moveDetailPanel: function (button, event) {
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


    /*      Getters     */

    getContentManagerToolbarMenu: function () {
        var menu = Ext.ComponentQuery.query('contentManagerToolbarMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.contentManagerToolbarMenu');
        }
        return menu;
    }



});
