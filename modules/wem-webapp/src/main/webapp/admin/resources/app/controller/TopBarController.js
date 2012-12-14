/**
 * Base controller for admin
 */
Ext.define('Admin.controller.TopBarController', {
    extend: 'Admin.controller.Controller',

    stores: [],
    models: [],
    views: [
        'Admin.view.TopBar'
    ],

    init: function () {
        this.control({
            '#mainViewport': {
                afterrender: {
                    fn: this.loadDefaultApplication,
                    delay: 10
                }
            },
            'startMenu': {
                tileclick: this.loadApplication
            }
        });
    },


    /*  Public  */

    loadDefaultApplication: function () {
        var defaultItem = {
            id: 'app-02',
            title: 'Dashboard',
            cls: 'span2 dashboard',
            iconCls: 'icon-metro-dashboard-24',
            appUrl: 'app-dashboard.jsp',
            defaultApp: true
        };
        this.loadApplication(defaultItem);
    },

    loadApplication: function (selectedMenuItem) {
        var me = this;
        var parent = this.getParentFrame();

        var callback = function () {
            var iframes = parent.Ext.DomQuery.select('iframe');
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
                me.appendIframe(parent, selectedMenuItem);
                me.showLoadMask();
            }

            me.setDocumentTitle(selectedMenuItem.text);
            me.setStartButton(selectedMenuItem);
        };

        // first close menu, then change app
        // to reduce flickering
        var startMenu = this.getStartMenu();
        if (startMenu && startMenu.isExpanded()) {
            startMenu.slideOut(callback);
        } else {
            callback.call(me);
        }
    },


    /*  Private */

    getParentFrame: function () {
        return window.parent.parent || window.parent;
    },

    appendIframe: function (parent, selectedMenuItem) {

        var iframe = parent.Ext.core.DomHelper.append('appFrames', {
            tag: 'iframe',
            src: selectedMenuItem.appUrl,
            id: 'iframe-' + selectedMenuItem.id,
            style: 'width: 100%; height: 100%; border: 0'
        }, false);
    },

    showLoadMask: function () {
        if (!window.appLoadMask) {
            window.appLoadMask = new Ext.LoadMask(Ext.getDom('appFrames'), {
                floating: {
                    shadow: false
                }
            });
        }

        window.appLoadMask.show();
    },

    setDocumentTitle: function (title) {
        window.document.title = 'Enonic WEM Admin - ' + title;
    },

//  this is needed in case of shared top bar
    setStartButton: function (selectedMenuItem) {
        var topBar = this.getTopBar();
        if (topBar) {
            var startButton = this.getStartButton();
            startButton.setText(selectedMenuItem.title);
            startButton.setIconCls(selectedMenuItem.iconCls);
        }
    }

});