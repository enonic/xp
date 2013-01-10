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
            }
        });

        this.application.on({
            loadApplication: {
                fn: this.loadApplication,
                scope: this
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
        var me = this,
            parent = this.getParentFrame(),
            iframes = parent.Ext.DomQuery.select('iframe');

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

        me.setStartButton(selectedMenuItem);
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
        var parent = window.parent.parent || window.parent;
        if (!parent.appLoadMask) {
            parent.appLoadMask = new Ext.LoadMask(this.getMainViewport());
        }
        parent.appLoadMask.show();
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