/**
 * Main file for all admin API classes and methods.
 */

///<reference path='Mousetrap.d.ts' />
///<reference path='jquery.d.ts' />

///<reference path='util/ImageLoader.ts' />
///<reference path='util/UriHelper.ts' />

///<reference path='model/Model.ts' />
///<reference path='model/SpaceModel.ts' />
///<reference path='model/ContentModel.ts' />
///<reference path='model/ContentTypeModel.ts' />

///<reference path='handler/DeleteSpaceParam.ts' />
///<reference path='handler/DeleteSpaceParamFactory.ts' />
///<reference path='handler/DeleteSpacesHandler.ts' />

///<reference path='lib/JsonRpcProvider.ts' />
///<reference path='lib/RemoteService.ts' />

///<reference path='event/Event.ts' />
///<reference path='event/EventBus.ts' />

///<reference path='ui/Action.ts' />

///<reference path='ui/ElementHelper.ts' />
///<reference path='ui/ImgElHelper.ts' />
///<reference path='ui/Element.ts' />
///<reference path='ui/DivEl.ts' />
///<reference path='ui/H1El.ts' />
///<reference path='ui/H2El.ts' />
///<reference path='ui/H3El.ts' />
///<reference path='ui/H4El.ts' />
///<reference path='ui/UlEl.ts' />
///<reference path='ui/LiEl.ts' />
///<reference path='ui/EmEl.ts' />
///<reference path='ui/ImgEl.ts' />
///<reference path='ui/Panel.ts' />
///<reference path='ui/DeckPanel.ts' />
///<reference path='ui/ButtonEl.ts' />
///<reference path='ui/BodyMask.ts' />
///<reference path='ui/AbstractButton.ts' />
///<reference path='ui/toolbar/Toolbar.ts' />
///<reference path='ui/menu/MenuItem.ts' />
///<reference path='ui/detailpanel/DetailPanel.ts' />
///<reference path='ui/wizard/WizardPanel.ts' />
///<reference path='ui/menu/ContextMenu.ts' />
///<reference path='ui/menu/ActionMenu.ts' />
///<reference path='ui/tab/Tab.ts' />
///<reference path='ui/tab/TabRemovedListener.ts' />
///<reference path='ui/tab/TabSelectedListener.ts' />
///<reference path='ui/tab/TabNavigator.ts' />
///<reference path='ui/tab/TabMenu.ts' />
///<reference path='ui/tab/TabMenuItem.ts' />
///<reference path='ui/tab/TabPanelController.ts' />
///<reference path='ui/toolbar/Toolbar.ts' />

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

