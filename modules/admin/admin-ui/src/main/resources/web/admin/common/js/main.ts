/**
 * Main file for all admin API classes and methods.
 */

declare var Mousetrap:MousetrapStatic;

/*
 Prefix must match @_CLS_PREFIX in web\admin\common\styles\_module.less
 */
api.StyleHelper.setCurrentPrefix(api.StyleHelper.ADMIN_PREFIX);

if (!api.BrowserHelper.isIE()) { // IE has slow performance if longStackSupport is true
    wemQ.longStackSupport = true;  //seems to give more readable stacktraces from errors thrown inside a Q Promise
}
