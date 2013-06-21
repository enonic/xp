module api_util {
    class ImageLoader {
        private static cachedImages;

        static get(url:string, width?:number, height?:number):HTMLImageElement;
    }
}
module api_util {
    var baseUri:string;

    function getAbsoluteUri(uri:string):string;
}
module api_util {
    class Animation {
        static DELAY:number;

        static start(doStep:Function, duration:number, delay?:number):number;

        static stop(id:number):void;
    }
}
module api_model {
    interface Model {
        data: any;
        id: string;
    }
}
module api_model {
    interface SpaceModel extends Model {
        data: {
            name: string;
            displayName: string;
            iconUrl: string;
            rootContentId: number;
            createdTime: Date;
            modifiedTime: Date;
            editable: bool;
            deletable: bool;
        };
    }
}
module api_model {
    interface ContentModel extends Model {
        data: {
            id: string;
            name: string;
            path: string;
            type: string;
            displayName: string;
            owner: string;
            modifier: string;
            iconUrl: string;
            modifiedTime: Date;
            createdTime: Date;
            editable: bool;
            deletable: bool;
            allowsChildren: bool;
            hasChildren: bool;
            leaf: bool;
        };
    }
}
module api_model {
    interface ContentTypeModel extends Model {
        data: {
            qualifiedName: string;
            name: string;
            displayName: string;
            module: string;
            iconUrl: string;
            configXML: string;
            createdTime: Date;
            modifiedTime: Date;
        };
    }
}
module api_handler {
    interface DeleteSpaceParam {
        spaceName: string[];
    }
}
module api_handler {
    class DeleteSpaceParamFactory {
        static create(spaces:api_model.SpaceModel[]):DeleteSpaceParam;
    }
}
module api_handler {
    class DeleteSpacesHandler {
        public doDelete(deleteSpaceParam:DeleteSpaceParam, callback:(thisArg:any, success:any, result:any) => void):void;
    }
}
module api_handler {
    interface DeleteContentParam {
        contentIds: string[];
    }
}
module api_handler {
    class DeleteContentParamFactory {
        static create(content:api_model.ContentModel[]):DeleteContentParam;
    }
}
module api_handler {
    class DeleteContentHandler {
        public doDelete(deleteContentParam:DeleteContentParam, callback:(thisArg:any, success:any, result:any) => void):void;
    }
}
module api_remote {
    class JsonRpcProvider {
        public ext:any;

        constructor(url:string, methods:string[], namespace:string);

        private initAPI(methods);

        private getCallData(transaction);

        private createEvent(response);
    }
}
module api_remote {
    var RemoteService:RemoteServiceInterface;
    interface RemoteCallResultBase {
        success: bool;
        error?: string;
    }
    interface RemoteCallSpaceListParams {
    }
    interface RemoteCallSpaceListResult extends RemoteCallResultBase {
        total: number;
        spaces: {
            createdTime: Date;
            deletable: bool;
            displayName: string;
            editable: bool;
            iconUrl: string;
            modifiedTime: Date;
            name: string;
            rootContentId: string;
        }[];
    }
    interface RemoteCallSpaceGetParams {
        spaceName: string[];
    }
    interface RemoteCallSpaceGetResult extends RemoteCallResultBase {
        total: number;
        space: {
            createdTime: Date;
            displayName: string;
            iconUrl: string;
            modifiedTime: Date;
            name: string;
            rootContentId: string;
        };
    }
    interface RemoteCallSpaceCreateOrUpdateParams {
        spaceName: string;
        displayName: string;
        iconReference?: string;
    }
    interface RemoteCallSpaceCreateOrUpdateResult extends RemoteCallResultBase {
        created: bool;
        updated: bool;
    }
    interface RemoteCallSpaceDeleteParams {
        spaceName: string[];
    }
    interface RemoteCallSpaceDeleteResult extends RemoteCallResultBase {
        deleted: bool;
        failureReason?: string;
    }
    interface RemoteServiceInterface {
        account_find(params, callback): void;
        account_getGraph(params, callback): void;
        account_changePassword(params, callback): void;
        account_verifyUniqueEmail(params, callback): void;
        account_suggestUserName(params, callback): void;
        account_createOrUpdate(params, callback): void;
        account_delete(params, callback): void;
        account_get(params, callback): void;
        util_getCountries(params, callback): void;
        util_getLocales(params, callback): void;
        util_getTimeZones(params, callback): void;
        userstore_getAll(params, callback): void;
        userstore_get(params, callback): void;
        userstore_getConnectors(params, callback): void;
        userstore_createOrUpdate(params, callback): void;
        userstore_delete(params, callback): void;
        content_createOrUpdate(params, callback): void;
        contentType_get(params, callback): void;
        content_list(params, callback): void;
        content_tree(params, callback): void;
        content_get(params, callback): void;
        contentType_list(params, callback): void;
        content_delete(params, callback): void;
        content_find(params, callback): void;
        content_validate(params, callback): void;
        contentType_createOrUpdate(params, callback): void;
        contentType_delete(params, callback): void;
        contentType_tree(params, callback): void;
        schema_tree(params, callback): void;
        schema_list(params, callback): void;
        system_getSystemInfo(params, callback): void;
        mixin_get(params, callback): void;
        mixin_createOrUpdate(params, callback): void;
        mixin_delete(params, callback): void;
        relationshipType_get(params, callback): void;
        relationshipType_createOrUpdate(params, callback): void;
        relationshipType_delete(params, callback): void;
        space_list(params:RemoteCallSpaceListParams, callback:(result:RemoteCallSpaceListResult) => void): void;
        space_get(params:RemoteCallSpaceGetParams, callback:(result:RemoteCallSpaceGetResult) => void): void;
        space_delete(params:RemoteCallSpaceDeleteParams, callback:(result:RemoteCallSpaceDeleteResult) => void): void;
        space_createOrUpdate(params:RemoteCallSpaceCreateOrUpdateParams,
                             callback:(result:RemoteCallSpaceCreateOrUpdateResult) => void): void;
        binary_create(params, callback): void;
    }
}
module api_event {
    class Event {
        private name;

        constructor(name:string);

        public getName():string;

        public fire():void;
    }
}
module api_event {
    function onEvent(name:string, handler:(event:Event) => void):void;

    function fireEvent(event:Event):void;
}
module api_notify {
    enum Type {
        INFO,
        ERROR,
        ACTION,
    }
    class Action {
        private name;
        private handler;

        constructor(name:string, handler:Function);

        public getName():string;

        public getHandler():Function;
    }
    class Message {
        private type;
        private text;
        private actions;

        constructor(type:Type, text:string);

        public getType():Type;

        public getText():string;

        public getActions():Action[];

        public addAction(name:string, handler:() => void):void;

        public send():void;
    }
    function newInfo(text:string):Message;

    function newError(text:string):Message;

    function newAction(text:string):Message;
}
module api_notify {
    class NotifyManager {
        private timers;
        private el;

        constructor();

        private render();

        private getWrapperEl();

        public notify(message:Message):void;

        private doNotify(opts);

        private setListeners(el, opts);

        private remove(el);

        private startTimer(el);

        private stopTimer(el);

        private renderNotification(opts);
    }
    function sendNotification(message:Message):void;
}
module api_notify {
    class NotifyOpts {
        public message:string;
        public backgroundColor:string;
        public listeners:Object[];
    }
    function buildOpts(message:Message):NotifyOpts;
}
module api_notify {
    function showFeedback(message:string):void;

    function updateAppTabCount(appId, tabCount:Number):void;
}
module api_dom {
    class ElementHelper {
        private el;

        static fromName(name:string):ElementHelper;

        constructor(element:HTMLElement);

        public getHTMLElement():HTMLElement;

        public insertBefore(newEl:Element, existingEl:Element):void;

        public setDisabled(value:bool):ElementHelper;

        public setId(value:string):ElementHelper;

        public setInnerHtml(value:string):ElementHelper;

        public setValue(value:string):ElementHelper;

        public addClass(clsName:string):void;

        public hasClass(clsName:string):bool;

        public removeClass(clsName:string):void;

        public addEventListener(eventName:string, f:(event:Event) => any):void;

        public removeEventListener(eventName:string, f:(event:Event) => any):void;

        public appendChild(child:HTMLElement):ElementHelper;

        public setData(name:string, value:string):ElementHelper;

        public getData(name:string):string;

        public getDisplay():string;

        public setDisplay(value:string):ElementHelper;

        public getVisibility():string;

        public setVisibility(value:string):ElementHelper;

        public setPosition(value:string):ElementHelper;

        public setWidth(value:string):ElementHelper;

        public getWidth():number;

        public setHeight(value:string):ElementHelper;

        public getHeight():number;

        public setTop(value:string):ElementHelper;

        public setLeft(value:string):ElementHelper;

        public setMarginLeft(value:string):ElementHelper;

        public setMarginRight(value:string):ElementHelper;

        public setMarginTop(value:string):ElementHelper;

        public setMarginBottom(value:string):ElementHelper;

        public setZindex(value:number):ElementHelper;

        public setBackgroundImage(value:string):ElementHelper;

        public remove():void;

        public getOffset():{
            top: number;
            left: number;
        };
    }
}
module api_dom {
    class ImgHelper extends ElementHelper {
        private el;

        static create():ElementHelper;

        constructor(element:HTMLImageElement);

        public getHTMLElement():HTMLImageElement;

        public setSrc(value:string):ImgHelper;
    }
}
module api_dom {
    class Element {
        private static constructorCounter;
        private el;
        private id;

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:ElementHelper);

        public show():void;

        public hide():void;

        public isVisible():bool;

        public empty():void;

        public getId():string;

        public getEl():ElementHelper;

        public getHTMLElement():HTMLElement;

        public appendChild(child:Element):void;

        public prependChild(child:Element):void;

        public removeChild(child:Element):void;

        public removeChildren():void;
    }
}
module api_dom {
    class DivEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class H1El extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class H2El extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class H3El extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class H4El extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class UlEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class LiEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class EmEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class ImgEl extends Element {
        constructor(src:string, idPrefix?:string, className?:string);

        public getEl():ImgHelper;
    }
}
module api_dom {
    class SpanEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_dom {
    class ButtonEl extends Element {
        constructor(idPrefix?:string, className?:string);
    }
}
module api_ui {
    class Action {
        private label;
        private iconClass;
        private enabled;
        private executionListeners;
        private propertyChangeListeners;

        constructor(label:string);

        public getLabel():string;

        public setLabel(value:string):void;

        public isEnabled():bool;

        public setEnabled(value:bool):void;

        public getIconClass():string;

        public setIconClass(value:string):void;

        public execute():void;

        public addExecutionListener(listener:(action:Action) => void):void;

        public addPropertyChangeListener(listener:(action:Action) => void):void;
    }
}
module api_ui {
    class Panel extends api_dom.DivEl {
        constructor(idPrefix?:string);
    }
}
module api_ui {
    class DeckPanel extends Panel {
        private panels;
        private panelShown;

        constructor(idPrefix?:string);

        public getSize():number;

        public addPanel(panel:Panel):number;

        public getPanel(index:number):Panel;

        public removePanel(index:number):Panel;

        private isShownPanel(panelIndex);

        public showPanel(index:number):void;

        public getPanels():Panel[];
    }
}
module api_ui {
    class BodyMask extends api_dom.DivEl {
        private static instance;

        static get():BodyMask;

        constructor();

        public activate():void;

        public deActivate():void;
    }
}
module api_ui {
    class AbstractButton extends api_dom.ButtonEl {
        private label;

        constructor(idPrefix:string, label:string);

        public setEnable(value:bool):void;
    }
}
module api_ui_toolbar {
    class Toolbar extends api_dom.DivEl {
        public ext;
        private components;

        constructor();

        private initExt();

        public addAction(action:api_ui.Action):void;

        public addElement(element:api_dom.Element):void;

        public addGreedySpacer():void;

        private addActionButton(action);

        private hasGreedySpacer();
    }
}
module api_ui_toolbar {
    class ToggleSlide extends api_dom.DivEl {
        private onText;
        private offText;
        private isOn;
        private thumb;
        private holder;
        private onLabel;
        private offLabel;
        private animationId;
        private animationDuration;

        constructor(onText:string, offText:string, initOn:bool);

        public toggle():void;

        public turnOn():void;

        public turnOff():void;

        public isTurnedOn():bool;

        private createMarkup();

        private calculateStyles();

        private addListeners();

        private slideLeft();

        private slideRight();

        private calculateOffset();

        private animate(step);
    }
}
module api_ui_menu {
    class MenuItem extends api_dom.LiEl {
        private action;

        constructor(action:api_ui.Action);

        public setEnable(value:bool):void;
    }
}
module api_ui_menu {
    class ContextMenu extends api_dom.UlEl {
        private menuItems;

        constructor(...actions:api_ui.Action[]);

        public addAction(action:api_ui.Action):void;

        private createMenuItem(action);

        public showAt(x:number, y:number):void;

        private hide();

        private hideMenuOnOutsideClick(evt);
    }
}
module api_ui_menu {
    class ActionMenu extends api_dom.UlEl {
        private ext;
        private button;
        private menuItems;

        constructor(...actions:api_ui.Action[]);

        public addAction(action:api_ui.Action):void;

        public getExt();

        public showBy(button:ActionMenuButton):void;

        private initExt();

        private createMenuItem(action);

        private hide();

        private hideMenuOnOutsideClick(evt);
    }
    class ActionMenuButton extends api_dom.ButtonEl {
        private ext;
        private menu;

        constructor(menu:ActionMenu);

        public setEnabled(value:bool):void;

        public getExt();

        private initExt();
    }
}
module api_ui_tab {
    interface Tab {
        setTabIndex(value:number);
        getTabIndex(): number;
        getLabel(): string;
        isVisible(): bool;
        isRemovable(): bool;
    }
}
module api_ui_tab {
    interface TabNavigator {
        addTab(tab:Tab);
        removeTab(tab:Tab);
        selectTab(tab:Tab);
        getActiveTab(): Tab;
        deselectTab();
        getSize(): number;
        addTabSelectedListener(listener:(Tab:any) => void);
        addTabRemoveListener(listener:(Tab:any) => bool);
    }
}
module api_ui_tab {
    class TabMenu extends api_dom.DivEl implements TabNavigator {
        public ext;
        private tabMenuButton;
        private menuEl;
        private showingMenuItems;
        private tabs;
        private selectedTab;
        private tabSelectedListeners;
        private tabRemovedListeners;

        constructor(idPrefix?:string);

        public createTabMenuButton():TabMenuButton;

        public createMenu():api_dom.UlEl;

        private initExt();

        private toggleMenu();

        public hideMenu():void;

        public showMenu():void;

        public addTab(tab:Tab):void;

        public getSize():number;

        public removeTab(tab:Tab):void;

        private isSelectedTab(tab);

        private isLastTab(tab);

        public selectTab(tab:Tab):void;

        public getActiveTab():Tab;

        public deselectTab():void;

        public addTabSelectedListener(listener:(Tab:any) => void):void;

        public addTabRemoveListener(listener:(Tab:any) => bool):void;

        public handleTabClickedEvent(tabMenuItem:TabMenuItem):void;

        public handleTabRemoveButtonClickedEvent(tabMenuItem:TabMenuItem):void;

        public fireTabSelected(tab:Tab):void;

        private fireTabRemoveEvent(tab);
    }
}
module api_ui_tab {
    class TabMenuButton extends api_dom.DivEl {
        private labelEl;
        private tabMenu;

        constructor(idPrefix?:string);

        public setTabMenu(tabMenu:TabMenu):void;

        public setLabel(value:string):void;
    }
}
module api_ui_tab {
    class TabMenuItem extends api_dom.LiEl implements Tab {
        private tabIndex;
        private label;
        private labelEl;
        private tabMenu;
        private visible;
        private removable;

        constructor(label:string);

        public setTabMenu(tabMenu:TabMenu):void;

        public setTabIndex(value:number):void;

        public getTabIndex():number;

        public getLabel():string;

        public isVisible():bool;

        public setVisible(value:bool):void;

        public isRemovable():bool;

        public setRemovable(value:bool):void;

        private remove();
    }
}
module api_ui_tab {
    class TabBar extends api_dom.DivEl implements TabNavigator {
        constructor(idPrefix?:string);

        public addTab(tab:Tab):void;

        public removeTab(tab:Tab):void;

        public getSize():number;

        public getActiveTab():Tab;

        public selectTab(tab:Tab):void;

        public deselectTab():void;

        public addTabSelectedListener(listener:(Tab:any) => void):void;

        public addTabRemoveListener(listener:(Tab:any) => bool):void;
    }
}
module api_ui_tab {
    class TabbedDeckPanel extends api_ui.DeckPanel {
        private navigator;

        constructor(navigator:TabNavigator);

        public addTab(tab:Tab, panel:api_ui.Panel):void;

        public showTab(tab:Tab):void;

        public tabRemove(tab:Tab):bool;
    }
}
module api_ui {
    class Tooltip extends api_dom.DivEl {
        private target;
        private timeout;
        private side;
        private offset;
        private hideTimeout;

        constructor(target:api_dom.Element, text:string, timeout?:number, side?:string, offset?:number[]);

        public show():void;

        public showFor(ms:number):void;

        public setTimeout(timeout:number):void;

        public getTimeout():number;

        public setSide(side:string):void;

        public getSide():string;

        private positionByTarget();

        private startTimeout(ms?);

        private stopTimeout();
    }
}
module api_ui {
    class ProgressBar extends api_dom.DivEl {
        private progress;
        private value;

        constructor(value?:number);

        public setValue(value:number):void;

        public getValue():number;

        private normalizeValue(value);
    }
}
module api_ui_grid {
    class TreeGridPanel {
        static GRID:string;
        static TREE:string;
        public ext:Ext_panel_Panel;
        private gridStore;
        private gridConfig;
        private columns;
        private treeStore;
        private treeConfig;
        private keyField;
        private activeList;
        private itemId;

        public create(region?:string, renderTo?:string):TreeGridPanel;

        constructor(columns:any[], gridStore:Ext_data_Store, treeStore:Ext_data_TreeStore, gridConfig?:Object, treeConfig?:Object);

        private createGridPanel(gridStore, gridConfig?);

        private createTreePanel(treeStore, treeConfig?);

        private fireUpdateEvent(values);

        public getActiveList():Ext_panel_Table;

        public setActiveList(listId):void;

        public setKeyField(keyField:string):void;

        public getKeyField():string;

        public setItemId(itemId:string):void;

        public getItemId():string;

        public refresh():void;

        public removeAll():void;

        public deselect(key):void;

        public getSelection():any[];

        public setRemoteSearchParams(params):void;

        public setResultCountVisible(visible):void;

        public updateResultCount(count):void;
    }
}
module api_appbar {
    class AppBar extends api_dom.DivEl {
        public ext;
        private appName;
        private actions;
        private launcherButton;
        private homeButton;
        private tabMenu;
        private userButton;
        private userInfoPopup;

        constructor(appName, actions:AppBarActions, tabMenu?:AppBarTabMenu);

        private initExt();

        public getTabMenu():AppBarTabMenu;
    }
    interface AppBarActions {
        showAppLauncherAction: api_ui.Action;
        showAppBrowsePanelAction: api_ui.Action;
    }
    class LauncherButton extends api_dom.ButtonEl {
        constructor(action:api_ui.Action);
    }
    class Separator extends api_dom.SpanEl {
        constructor();
    }
    class HomeButton extends api_dom.ButtonEl {
        constructor(text:string, action:api_ui.Action);
    }
    class TabMenuContainer extends api_dom.DivEl {
        constructor();
    }
    class UserButton extends api_dom.ButtonEl {
        constructor();

        public setIcon(photoUrl:string):void;
    }
}
module api_appbar {
    class UserInfoPopup extends api_dom.DivEl {
        private isShown;

        constructor();

        private createContent();

        private render();

        public toggle():void;
    }
}
module api_appbar {
    class ShowAppLauncherEvent extends api_event.Event {
        constructor();

        static on(handler:(event:ShowAppLauncherEvent) => void):void;
    }
    class ShowAppBrowsePanelEvent extends api_event.Event {
        constructor();

        static on(handler:(event:ShowAppBrowsePanelEvent) => void):void;
    }
}
module api_appbar {
    class AppBarTabMenu extends api_ui_tab.TabMenu {
        private tabMenuButton;

        constructor(idPrefix?:string);

        public addTab(tab:api_ui_tab.Tab):void;

        public createTabMenuButton():api_ui_tab.TabMenuButton;

        public removeTab(tab:api_ui_tab.Tab):void;
    }
}
module api_appbar {
    class AppBarTabMenuButton extends api_ui_tab.TabMenuButton {
        private iconEl;
        private tabCountEl;

        constructor(idPrefix?:string);

        public setTabCount(value:number):void;
    }
    class AppBarTabCount extends api_dom.SpanEl {
        constructor();

        public setCount(value:number):void;
    }
}
module api_appbar {
    class AppBarTabMenuItem extends api_ui_tab.TabMenuItem {
        constructor(label:string);
    }
}
module api {
    class AppPanel extends api_ui_tab.TabbedDeckPanel {
        private homePanel;

        constructor(appBar:api_ui_tab.TabNavigator, homePanel:api_ui.Panel);

        public showBrowsePanel():void;
    }
}
module api_ui_dialog {
    class DialogButton extends api_ui.AbstractButton {
        private action;

        constructor(action:api_ui.Action);
    }
}
module api_ui_dialog {
    interface ModalDialogConfig {
        title: string;
        width: number;
        height: number;
    }
    class ModalDialog extends api_dom.DivEl {
        private config;
        private title;
        private contentPanel;
        private buttonRow;

        constructor(config:ModalDialogConfig);

        public setTitle(value:string):void;

        public appendChildToContentPanel(child:api_dom.Element):void;

        public addAction(action:api_ui.Action):void;

        public show():void;

        public hide():void;

        public close():void;

        public open():void;
    }
    class ModalDialogTitle extends api_dom.H2El {
        constructor(title:string);

        public setTitle(value:string):void;
    }
    class ModalDialogContentPanel extends api_dom.DivEl {
        constructor();
    }
    class ModalDialogButtonRow extends api_dom.DivEl {
        constructor();

        public addAction(action:api_ui.Action):void;
    }
    class ModalDialogButton extends api_ui.AbstractButton {
        private action;

        constructor(action:api_ui.Action);
    }
    class ModalDialogCancelAction extends api_ui.Action {
        constructor();
    }
}
module api_delete {
    class DeleteItem {
        private iconUrl;
        private displayName;

        constructor(iconUrl:string, displayName:string);

        public getDisplayName():string;

        public getIconUrl():string;
    }
}
module api_delete {
    class DeleteDialog extends api_ui_dialog.ModalDialog {
        private modelName;
        private deleteAction;
        private cancelAction;
        private deleteItems;
        private itemList;

        constructor(modelName:string);

        public setDeleteAction(action:api_ui.Action):void;

        public setDeleteItems(deleteItems:DeleteItem[]):void;
    }
    class CancelDeleteDialogAction extends api_ui.Action {
        constructor();
    }
    class DeleteDialogItemList extends api_dom.DivEl {
        constructor();

        public clear():void;
    }
}
module api_browse {
    class AppBrowsePanel extends api_ui.Panel {
        public ext;
        private browseToolbar;
        private grid;
        private detailPanel;
        private filterPanel;

        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, detailPanel:DetailPanel, filterPanel:any);

        public init():void;

        private initExt();
    }
}
module api_browse {
    class DetailPanel extends api_dom.DivEl {
        public ext;

        constructor();

        private initExt();
    }
    class DetailTabPanel extends api_dom.DivEl {
        private model;
        private navigation;
        private tabs;
        private canvas;
        private tabChangeCallback;
        private actionMenu;

        constructor(model:api_model.Model);

        private addHeader(title, subtitle, iconUrl);

        private addCanvas();

        public setTabChangeCallback(callback:(DetailPanelTab:any) => void):void;

        public addTab(tab:DetailPanelTab):void;

        public setActiveTab(tab:DetailPanelTab):void;

        public addAction(action:api_ui.Action):void;

        private createActionMenu();

        private addNavigation();
    }
    class DetailPanelTab {
        public name:string;
        public content:api_dom.Element;

        constructor(name:string);
    }
    class DetailPanelTabList extends api_dom.UlEl {
        private tabs;

        constructor();

        public addTab(tab, clickCallback:(DetailPanelTab:any) => void):void;

        private selectTab(tab);
    }
    class DetailPanelBox extends api_dom.DivEl {
        private model;

        constructor(model:any, removeCallback?:(DetailPanelBox:any) => void);

        private addRemoveButton(callback?);

        private setIcon(iconUrl, size);

        private setData(title, subtitle);

        public getModel():api_model.Model;
    }
}
module api_wizard {
    class FormIcon extends api_dom.ButtonEl {
        public iconUrl:string;
        public iconTitle:string;
        public uploadUrl:string;
        public ext;
        private uploader;
        private img;
        private progress;
        private tooltip;

        constructor(iconUrl:string, iconTitle:string, uploadUrl?:string);

        private initExt();

        private initUploader(elId);

        public setSrc(src:string):void;
    }
}
module api_wizard {
    class WizardPanel extends api_ui.Panel {
        private steps;
        private stepContainer;
        private wizardStepPanels;
        private titleEl;
        private subTitleEl;
        public ext;

        constructor();

        private initExt();

        public setTitle(title:string):void;

        public setSubtitle(subtitle:string):void;

        public addStep(step:WizardStep):void;

        public addIcon(icon:FormIcon):void;

        public addToolbar(toolbar:api_ui_toolbar.Toolbar):void;

        private addTitle();

        private addSubTitle();

        private addStepContainer();
    }
    class WizardStepPanels extends api_ui.DeckPanel {
        constructor();
    }
    class WizardStepContainer extends api_dom.UlEl {
        private deckPanel;
        private steps;

        constructor(deckPanel:WizardStepPanels);

        public addStep(step:WizardStep):void;

        private removeActive();
    }
    class WizardStep {
        private label;
        private panel;
        private active;
        private el;

        constructor(label:string, panel:api_ui.Panel);

        public setEl(el:api_dom.Element):void;

        public setActive(active:bool):void;

        public isActive():bool;

        public getEl():api_dom.Element;

        public getLabel():string;

        public getPanel():api_ui.Panel;
    }
}
module api_content_data {
    class DataId {
        private name;
        private arrayIndex;
        private refString;

        constructor(name:string, arrayIndex:number);

        public getName():string;

        public getArrayIndex():number;

        public toString():string;

        static from(str:string):DataId;
    }
}
module api_content_data {
    class Data {
        private name;
        private arrayIndex;
        private parent;

        constructor(name:string);

        public setArrayIndex(value:number):void;

        public setParent(parent:DataSet):void;

        public getId():DataId;

        public getName():string;

        public getParent():Data;

        public getArrayIndex():number;
    }
}
module api_content_data {
    class DataSet extends Data {
        private dataById;

        constructor(name:string);

        public nameCount(name:string):number;

        public addData(data:Data):void;

        public getData(dataId:string):Data;
    }
}
module api_content_data {
    class ContentData extends DataSet {
        constructor();
    }
}
module api_content_data {
    class Property extends Data {
        private value;
        private type;

        static from(json):Property;

        constructor(name:string, value:string, type:string);

        public getValue():string;

        public getType():string;

        public setValue(value:any):void;
    }
}
module api_schema_content_form {
    class FormItem {
        private name;

        constructor(name:string);

        public getName():string;
    }
}
module api_schema_content_form {
    class InputType {
        private name;

        constructor(json:any);

        public getName():string;
    }
}
module api_schema_content_form {
    class Input extends FormItem {
        private inputType;
        private label;
        private immutable;
        private occurrences;
        private indexed;
        private customText;
        private validationRegex;
        private helpText;

        constructor(json);

        public getLabel():string;

        public isImmutable():bool;

        public getOccurrences():Occurrences;

        public isIndexed():bool;

        public getCustomText():string;

        public getValidationRegex():string;

        public getHelpText():string;
    }
}
module api_schema_content_form {
    class Occurrences {
        private minimum;
        private maximum;

        constructor(json);
    }
}
