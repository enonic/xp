Ext.define('Admin.view.contentManager.contextwindow.TitleBar', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowTitleBar',
    uses: 'Admin.view.contentManager.contextwindow.Helper',

    cls: 'admin-context-window-title-bar',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    contextWindow: undefined,

    menuButton: undefined,

    titleText: undefined,

    toggleButton: undefined,

    currentWindowHeight: undefined,

    isCollapsed: false,

    initComponent: function () {

        this.menuButton = this.createMenuButton();
        this.titleText = this.createTitleTextCmp();
        this.toggleButton = this.createToggleButton();

        this.items = [
            this.menuButton,
            this.titleText,
            this.toggleButton
        ];

        this.callParent(arguments);
    },

    /**
     * @returns {Ext.button.Button}
     */
    createMenuButton: function () {
        var me = this;
        return new Ext.button.Button({
            text: '',
            arrowCls: '-none',
            cls: 'admin-context-window-menu-button icon-reorder',
            menu: new Ext.menu.Menu({
                border: false,
                plain: true,
                shadow: false,
                cls: 'context-window-menu',
                listeners: {
                    beforeshow: function (menu) {
                        menu.setWidth(me.getWidth());
                    }
                }
            })
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createTitleTextCmp: function () {
        return new Ext.Component({
            cls: 'admin-context-window-title-text',
            html: 'Title',
            flex: 3
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createToggleButton: function () {
        var me = this;
        return new Ext.Component({
            cls: 'admin-context-window-expand-collapse-button icon-chevron-down',
            width: 30,
            listeners: {
                render: function (cmp) {
                    cmp.getEl().on('click', function () {
                        if (me.isCollapsed) {
                            // fixme: is there a simpler way to toggle classes?
                            cmp.getEl().addCls('icon-chevron-down').removeCls('icon-chevron-up');
                        } else {
                            cmp.getEl().addCls('icon-chevron-up').removeCls('icon-chevron-down');
                        }
                        me.toggleExpandCollapseWindow();
                    });
                }
            }
        });
    },

    setTitleText: function (text) {
        this.titleText.getEl().setHTML(text);
    },

    toggleExpandCollapseWindow: function () {
        if (this.isCollapsed) {
            this.contextWindow.animate({
                duration: 200,
                to: {
                    height: this.currentWindowHeight
                }
            });
            this.isCollapsed = false;
        } else {
            this.contextWindow.animate({
                duration: 200,
                to: {
                    height: this.height
                }
            });

            // this.currentWindowHeight = this.height;
            this.isCollapsed = true;
        }
    },

    addMenuItem: function (text, callback) {
        var menuItemSpec = {
            plain: true,
            cls: 'context-window-menu-item',
            width: '100%',
            text: text,
            handler: function (item) {
                callback();
            }
        };

        this.menuButton.menu.add(menuItemSpec);
    }


});