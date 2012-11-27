Ext.define('Admin.controller.LauncherToolbarController', {
    extend: 'Admin.controller.Controller',

    views: ['Admin.view.LauncherToolbar'],

    requires: [
        'Admin.lib.LauncherToolbarHelper'
    ],

    init: function () {
        this.control(
            {
                'viewport': {
                    afterrender: {
                        fn: this.loadDefaultApplication,
                        delay: 10
                    }
                },
                'launcherToolbar *[id=app-launcher-logo]': {
                    render: this.onLogoRendered
                },
                'startMenu': {
                    tileclick: this.loadApplication
                }
            }
        );
    },

    loadDefaultApplication: function () {
        var defaultItem;
        var launcherToolbar = this.getLauncherToolbar();
        var startMenu = launcherToolbar.getStartMenu();
        if (startMenu.tiles) {
            // find default app
            Ext.Array.each(startMenu.tiles, function (tile) {
                if (tile.defaultApp) {
                    defaultItem = tile;
                    return false;
                }
            });
            // fall back to first otherwise
            if (!defaultItem && startMenu.tiles.length > 0) {
                defaultItem = startMenu.tiles[0];
            }
            if (defaultItem) {
                this.loadApplication(startMenu, defaultItem, null, null);
            }
        }
    },

    loadApplication: function (startMenu, selectedMenuItem, e, options) {
        var me = this;
        var callback = function () {
            var dom = Ext.DomQuery;

            var iframes = dom.select('iframe');
            var iframeExist = false;
            Ext.each(iframes, function (iframe, index, allIFrames) {
                if (iframe.id === 'iframe-' + selectedMenuItem.id) {
                    iframeExist = true;
                    iframe.style.display = 'block';
                } else {
                    iframe.style.display = 'none';
                }
            });

            if (!iframeExist) {
                me.appendIframe(selectedMenuItem);
                me.showLoadMask();
            }

            me.setDocumentTitle(selectedMenuItem.text);
            me.updateStartButton(selectedMenuItem);
        };
        // first close menu, then change app
        // to reduce flickering
        if (startMenu.isExpanded()) {
            startMenu.slideOut(callback);
        } else {
            callback.call(me);
        }
    },

    appendIframe: function (selectedMenuItem) {

        var iframe = Ext.core.DomHelper.append('app-frames', {
            tag: 'iframe',
            src: selectedMenuItem.appUrl,
            id: 'iframe-' + selectedMenuItem.id,
            style: 'width: 100%; height: 100%; border: 0'
        }, false);
    },

    updateStartButton: function (selectedMenuItem) {
        var startButton = this.getLauncherToolbar().getStartButton();
        startButton.setText(selectedMenuItem.title);
        startButton.setIconCls(selectedMenuItem.iconCls);
    },

    setDocumentTitle: function (title) {
        window.document.title = 'Enonic WEM Admin - ' + title;
    },

    showLoadMask: function () {
        if (!window.appLoadMask) {
            window.appLoadMask = new Ext.LoadMask(Ext.getDom('main-viewport-center'), {
                floating: {
                    shadow: false
                }
            });
        }

        window.appLoadMask.show();
    },


    onLogoRendered: function (component, options) {
        component.el.on('click', this.showAboutWindow);
    },

    showAboutWindow: function () {
        var aboutWindow = Ext.ComponentQuery.query('#admin-about-window')[0];
        if (aboutWindow) {
            aboutWindow.show();
            return;
        }

        Ext.create('Ext.window.Window', {
            itemId: 'admin-about-window',
            modal: true,
            resizable: false,
            title: 'About',
            width: 550,
            height: 300
        }).show();
    }

});
