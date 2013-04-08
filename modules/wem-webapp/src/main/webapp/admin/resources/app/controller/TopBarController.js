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
        // Custom event listeners
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
            id: 'app-1000',
            name: 'Blank',
            description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
            appUrl: 'blank.html'
        };
        this.loadApplication(defaultItem);
    },

    loadApplication: function (appData, urlHash) {
        var me = this,
            parent = this.getParentFrame(),
            iFrames = parent.Ext.DomQuery.select('iframe');

        var iFrameExist = false;
        Ext.each(iFrames, function (iframe, index, allIFrames) {
            if (iframe.id === 'iframe-' + appData.id) {
                iFrameExist = true;
                iframe.style.display = 'block';
            } else {
                iframe.style.display = 'none';
            }
        });

        if (!iFrameExist) {
            me.appendIframe(parent, appData, urlHash);
            me.showLoadMask();
        }

        me.setStartButton(appData);
    },


    /*  Private */

    getParentFrame: function () {
        return window.parent.parent || window.parent;
    },


    appendIframe: function (parent, appData, urlHash) {
        var url = appData.appUrl + '?appId=' + appData.id;
        if (urlHash) {
            url += urlHash;
        }

        var iFrameSpec = parent.Ext.core.DomHelper.append('admin-application-frames', {
            tag: 'iframe',
            src: url,
            id: 'iframe-' + appData.id,
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