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
            id: 'app-1000',
            name: 'Blank',
            description: 'Ei veri vituperata cum, no habeo dicta diceret vis. Eirmod audiam efficiendi quo ne, et duo decore epicurei. In veri liber movet usu, posse lorem erroribus at usu. Mea in vidisse mentitum repudiare, invidunt sensibus adipiscing ea nec. Illud munere te sit, ad singulis definitionem his.',
            appUrl: 'blank.html'
        };
        this.loadApplication(defaultItem);
    },

    loadApplication: function (appModel) {
        var me = this,
            parent = this.getParentFrame(),
            iframes = parent.Ext.DomQuery.select('iframe');

        var iframeExist = false;
        Ext.each(iframes, function (iframe, index, allIFrames) {
            if (iframe.id === 'iframe-' + appModel.id) {
                iframeExist = true;
                iframe.style.display = 'block';
            } else {
                iframe.style.display = 'none';
            }
        });

        if (!iframeExist) {
            me.appendIframe(parent, appModel);
            me.showLoadMask();
        }

        me.setStartButton(appModel);
    },


    /*  Private */

    getParentFrame: function () {
        return window.parent.parent || window.parent;
    },


    appendIframe: function (parent, appModel) {

        var iframe = parent.Ext.core.DomHelper.append('appFrames', {
            tag: 'iframe',
            src: appModel.appUrl + '?appId=' + appModel.id,
            id: 'iframe-' + appModel.id,
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