/**
 * Main file for all admin API classes and methods.
 */

///<reference path='util/UriHelper.ts' />

///<reference path='event/Event.ts' />
///<reference path='event/EventBus.ts' />

///<reference path='notify/Message.ts' />
///<reference path='notify/NotifyManager.ts' />
///<reference path='notify/NotifyOpts.ts' />
///<reference path='notify/MessageBus.ts' />

///<reference path='content/data/DataId.ts' />
///<reference path='content/data/Data.ts' />
///<reference path='content/data/DataSet.ts' />
///<reference path='content/data/Property.ts' />

///<reference path='schema/content/form/FormItem.ts' />
///<reference path='schema/content/form/InputType.ts' />
///<reference path='schema/content/form/Input.ts' />
///<reference path='schema/content/form/Occurrences.ts' />

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

