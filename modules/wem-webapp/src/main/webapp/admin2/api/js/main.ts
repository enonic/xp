/**
 * Main file for all admin API classes and methods.
 */

///<reference path='Mousetrap.d.ts' />

///<reference path='util/UriHelper.ts' />

///<reference path='event/Event.ts' />
///<reference path='event/EventBus.ts' />

///<reference path='action/Action.ts' />

///<reference path='ui/HTMLElementHelper.ts' />
///<reference path='ui/HTMLImageElementHelper.ts' />
///<reference path='ui/AbstractEl.ts' />
///<reference path='ui/DivEl.ts' />
///<reference path='ui/H1El.ts' />
///<reference path='ui/H2El.ts' />
///<reference path='ui/H3El.ts' />
///<reference path='ui/H4El.ts' />
///<reference path='ui/UlEl.ts' />
///<reference path='ui/LiEl.ts' />
///<reference path='ui/ImgEl.ts' />
///<reference path='ui/ButtonEl.ts' />
///<reference path='ui/BodyMask.ts' />
///<reference path='ui/AbstractButton.ts' />
///<reference path='ui/toolbar/Toolbar.ts' />
///<reference path='ui/menu/MenuItem.ts' />
///<reference path='ui/menu/ContextMenu.ts' />
///<reference path='ui/menu/ActionMenu.ts' />

///<reference path='ui/dialog/DialogButton.ts' />
///<reference path='ui/dialog/ModalDialog.ts' />
///<reference path='delete/DeleteItem.ts' />
///<reference path='delete/DeleteDialog.ts' />

///<reference path='notify/Message.ts' />
///<reference path='notify/NotifyManager.ts' />
///<reference path='notify/NotifyOpts.ts' />
///<reference path='notify/MessageBus.ts' />

///<reference path='content/data/DataId.ts' />
///<reference path='content/data/Data.ts' />
///<reference path='content/data/DataSet.ts' />
///<reference path='content/data/ContentData.ts' />
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

