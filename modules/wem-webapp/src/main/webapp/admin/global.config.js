(function () {
    // Define this property here so JS tools in the IDE recognizes it
    window.CONFIG = {};

    Ext.Loader.setConfig({
        enabled: true,
        disableCaching: true,
        paths: {
            'Admin': 'resources/app'
        }
    });
    Ext.require('Admin.lib.RemoteService');

    Ext.override(Ext.LoadMask, {
        floating: {
            shadow: false
        },
        msg: undefined,
        cls: 'admin-load-mask'
    });

    Ext.override(Ext.view.Table, {
        loadingText: undefined,
        loadingCls: 'x-mask-loading'
    });

    Ext.override(Ext.tree.View, {
        loadingText: undefined,
        loadingCls: 'x-mask-loading'
    });

    // TODO: Refactor
    function cms_getParentWindow() {
        return window.parent.parent || window.parent;
    }


    function cms_hideLoadMaskOnLoad() {
        if (window.addEventListener) {
            window.addEventListener('load', function () {
                cms_getParentWindow().appLoadMask.hide();
            }, false);
        } else if (window.attachEvent) { // IE
            window.attachEvent('onload', function () {
                cms_getParentWindow().appLoadMask.hide();
            });
        }
    }

    function cms_hideMainMenusOnClick() {
        if (window.addEventListener) {
            window.addEventListener('click', function () {
                //cms_getParentWindow().Admin.lib.LauncherToolbarHelper.hideLauncherMenus();
            }, false);
        } else if (window.attachEvent) { // IE
            window.attachEvent('onclick', function () {
                //cms_getParentWindow().Admin.lib.LauncherToolbarHelper.hideLauncherMenus();
            });

        }
    }

    function cms_launcherExist() {
        return cms_getParentWindow().frames.length > 0;
    }

    if (cms_launcherExist()) {
        cms_hideLoadMaskOnLoad();
        cms_hideMainMenusOnClick();
    }
}());
