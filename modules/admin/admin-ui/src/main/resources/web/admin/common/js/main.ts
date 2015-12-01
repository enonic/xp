/**
 * Main file for all admin API classes and methods.
 */

declare var Mousetrap:MousetrapStatic;

/*
 clsPrefix must match @_CLS_PREFIX in web\admin\common\styles\_module.less
 */
declare var clsPrefix: string;
clsPrefix = api.StyleHelper.COMMON_PREFIX;

wemQ.longStackSupport = true;