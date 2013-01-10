Ext.define('Admin.view.TopBar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.topBar',

    requires: [
        'Admin.view.TopBarMenu',
        'Admin.view.AdminImageButton'
    ],

    buttonAlign: 'center',
    cls: 'admin-topbar-panel',

    dock: 'top',
    plain: true,
    border: false,

    initComponent: function () {
        var me = this;

        this.startButton = Ext.create('Ext.button.Button', {
            xtype: 'button',
            itemId: 'app-launcher-button',
            margins: '0 8px 0 0',
            cls: 'start-button',
            handler: function (btn, evt) {
                me.toggleHomeScreen();
            }
        });

        this.homeButton = Ext.create('Ext.button.Button', {
            text: me.appName || '&lt; app name &gt;',
            cls: 'home-button',
            handler: function (btn, evt) {
                if (me.tabPanel) {
                    me.tabPanel.setActiveTab(0);
                }
            }
        });

        this.leftContainer = Ext.create('Ext.Container', {
            flex: 5,
            margins: '0 8px',
            layout: 'hbox'
        });
        this.titleText = Ext.create('Ext.form.Label');
        this.rightContainer = Ext.create('Ext.Container', {
            flex: 5,
            margins: '0 8px',
            layout: {
                type: 'hbox',
                align: 'middle',
                pack: 'end'
            }
        });

        this.items = [
            me.startButton,
            me.homeButton,
            me.leftContainer,
            me.titleText,
            me.rightContainer,
            {
                xtype: 'adminImageButton',
                icon: "rest/account/image/default/user",
                popupTpl: Templates.common.userPopUp,
                popupData: {
                    userName: "Thomas Lund Sigdestad",
                    photoUrl: "rest/account/image/default/user",
                    qName: 'system/tsi'
                }
            }
        ];

        if (this.tabPanel) {
            this.tabMenu = Ext.create('Admin.view.TopBarMenu', {
                tabPanel: me.tabPanel
            });
            this.titleButton = Ext.create('Ext.button.Button', {
                cls: 'title-button',
                menuAlign: 't-b?',
                menu: me.tabMenu,
                text: 'Title'
            });
            Ext.Array.insert(me.items, 3, [me.titleButton]);
        }

        this.callParent(arguments);
        this.syncTabCount();
    },


    toggleHomeScreen: function () {
        var isInsideIframe = window.top !== window.self;
        if (isInsideIframe) {
            window.parent.Ext.getCmp('admin-home-main-container').toggleShowHide();
        } else {
            console.error('Can not toggle home screen. Document must be loaded inside the main window');
        }
    },


    /*  Methods for Admin.view.TabPanel integration */

    insert: function (index, cfg) {
        var added = this.tabMenu.addItems(cfg);
        this.syncTabCount();

        return added.length === 1 ? added[0] : added;
    },

    setActiveTab: function (tab) {
        this.setTitle(this.getMenuItemDescription(tab.card));
    },

    remove: function (tab) {

        var removed = this.tabMenu.removeItems(tab);
        this.syncTabCount();

        return removed;
    },

    findNextActivatable: function (tab) {
        if (this.tabPanel) {
            // set first browse tab active
            return this.tabPanel.items.get(0);
        }
    },

    createMenuItemFromTab: function (item) {
        var me = this;
        var cfg = item.initialConfig || item;
        var data = item.data || item;
        return {
            tabBar: me,
            card: item,
            disabled: cfg.disabled,
            closable: cfg.closable,
            hidden: cfg.hidden && !item.hiddenByLayout, // only hide if it wasn't hidden by the layout itself
            iconCls: cfg.iconCls,
            iconSrc: data.image_url,
            editing: cfg.editing || false,
            text1: Ext.String.ellipsis(cfg.title || 'first line', 26),
            text2: Ext.String.ellipsis(me.getMenuItemDescription(item), 38)
        };
    },


    /*  Private */

    syncTabCount: function () {
        if (this.tabMenu && this.titleButton) {
            var tabCount = this.tabMenu.getAllItems(false).length;
            // show dropdown button when any tab is open or text when nothing is open
            this.titleButton.setVisible(tabCount > 0);
            this.titleText.setVisible(tabCount == 0);
        }
    },

    getMenuItemDescription: function (card) {
        var desc;
        if (card.data) {
            var data = card.data;
            desc = data.path || data.qualifiedName || data.displayName;
            if (!desc && data.content) {
                var content = data.content;
                desc = content.path || content.qualifiedName || content.displayName;
            }
        }
        if (!desc) {
            desc = card.title;
        }
        return desc;
    },


    /*  Public  */

    setTitle: function (text) {
        // highlight the last path fragment
        var title = text;
        var lastSlash = !Ext.isEmpty(text) ? text.lastIndexOf('/') : -1;
        if (lastSlash > -1) {
            title = text.substring(0, lastSlash + 1) + '<strong>' + text.substring(lastSlash + 1) + '</strong>';
        }
        this.titleText.setText(title);
        this.titleButton.setText(title);
    },

    getStartButton: function () {
        return this.startButton;
    },

    getLeftContainer: function () {
        return this.leftContainer;
    },

    getRightContainer: function () {
        return this.rightContainer;
    }
});
