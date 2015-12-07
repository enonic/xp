/**
 * Main file for all admin API classes and methods.
 */

declare var Mousetrap:MousetrapStatic;

/*
 Prefix must match @_CLS_PREFIX in web\admin\common\styles\_module.less
 */
api.StyleHelper.setCurrentPrefix(api.StyleHelper.ADMIN_PREFIX);

wemQ.longStackSupport = true;