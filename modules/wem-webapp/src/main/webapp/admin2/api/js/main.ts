/**
 * Main file for all admin API classes and methods.
 */

///<reference path='notify/MessageBus.ts' />
///<reference path='notify/NotificationManager.ts' />

///<reference path='util/UriHelper.ts' />

///<reference path='event/Event.ts' />
///<reference path='event/EventBus.ts' />


declare var Ext;
declare var Admin;

Ext.Loader.setConfig({
    enabled: false,
    disableCaching: false
});

Ext.override(Ext.LoadMask, {
    floating: {
        shadow: false
    },
    msg: undefined,
    cls: 'admin-load-mask',
    msgCls: 'admin-load-text',
    maskCls: 'admin-mask-white'
});

