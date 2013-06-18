module api_util {
    class ImageLoader {
        private static cachedImages;
        static get(url: string, width?: number, height?: number): HTMLImageElement;
    }
}
module api_util {
    var baseUri: string;
    function getAbsoluteUri(uri: string): string;
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
        static create(spaces: api_model.SpaceModel[]): DeleteSpaceParam;
    }
}
module api_handler {
    class DeleteSpacesHandler {
        public doDelete(deleteSpaceParam: DeleteSpaceParam, callback: (thisArg: any, success: any, result: any) => void): void;
    }
}
module api_remote {
    class JsonRpcProvider {
        public ext: any;
        constructor(url: string, methods: string[], namespace: string);
        private initAPI(methods);
        private getCallData(transaction);
        private createEvent(response);
    }
}
module api_remote {
    var RemoteService: RemoteServiceInterface;
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
        space_list(params: RemoteCallSpaceListParams, callback: (result: RemoteCallSpaceListResult) => void): void;
        space_get(params: RemoteCallSpaceGetParams, callback: (result: RemoteCallSpaceGetResult) => void): void;
        space_delete(params, callback): void;
        space_createOrUpdate(params, callback): void;
        binary_create(params, callback): void;
    }
}
module api_event {
    class Event {
        private name;
        constructor(name: string);
        public getName(): string;
        public fire(): void;
    }
}
module api_event {
    function onEvent(name: string, handler: (event: Event) => void): void;
    function fireEvent(event: Event): void;
}
module api_ui {
    class Action {
        private label;
        private iconClass;
        private enabled;
        private executionListeners;
        private propertyChangeListeners;
        constructor(label: string);
        public getLabel(): string;
        public setLabel(value: string): void;
        public isEnabled(): bool;
        public setEnabled(value: bool): void;
        public getIconClass(): string;
        public setIconClass(value: string): void;
        public execute(): void;
        public addExecutionListener(listener: (action: Action) => void): void;
        public addPropertyChangeListener(listener: (action: Action) => void): void;
    }
}
module api_ui {
    class ElementHelper {
        private el;
        static fromName(name: string): ElementHelper;
        constructor(element: HTMLElement);
        public getHTMLElement(): HTMLElement;
        public setDisabled(value: bool): ElementHelper;
        public setId(value: string): ElementHelper;
        public setInnerHtml(value: string): ElementHelper;
        public setValue(value: string): ElementHelper;
        public addClass(clsName: string): void;
        public hasClass(clsName: string): bool;
        public removeClass(clsName: string): void;
        public addEventListener(eventName: string, f: (event: Event) => any): void;
        public appendChild(child: HTMLElement): void;
        public setData(name: string, value: string): ElementHelper;
        public getData(name: string): string;
        public getDisplay(): string;
        public setDisplay(value: string): ElementHelper;
        public setPosition(value: string): ElementHelper;
        public setWidth(value: string): ElementHelper;
        public setHeight(value: string): ElementHelper;
        public setTop(value: string): ElementHelper;
        public setLeft(value: string): ElementHelper;
        public setMarginLeft(value: string): ElementHelper;
        public setMarginRight(value: string): ElementHelper;
        public setMarginTop(value: string): ElementHelper;
        public setMarginBottom(value: string): ElementHelper;
        public setZindex(value: number): ElementHelper;
        public setBackgroundImage(value: string): ElementHelper;
        public remove(): void;
    }
}
module api_ui {
    class ImgHelper extends ElementHelper {
        private el;
        static create(): ElementHelper;
        constructor(element: HTMLImageElement);
        public getHTMLElement(): HTMLImageElement;
        public setSrc(value: string): ImgHelper;
    }
}
module api_ui {
    class Element {
        private static constructorCounter;
        private el;
        private id;
        constructor(elementName: string, idPrefix?: string, className?: string, elHelper?: ElementHelper);
        public show(): void;
        public hide(): void;
        public empty(): void;
        public getId(): string;
        public getEl(): ElementHelper;
        public getHTMLElement(): HTMLElement;
        public appendChild(child: Element): void;
        public prependChild(child: Element): void;
        public removeChildren(): void;
    }
}
module api_ui {
    class DivEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class H1El extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class H2El extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class H3El extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class H4El extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class UlEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class LiEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class EmEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class ImgEl extends Element {
        constructor(src: string, idPrefix?: string, className?: string);
        public getEl(): ImgHelper;
    }
}
module api_ui {
    class SpanEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class Panel extends DivEl {
        constructor(idPrefix?: string);
    }
}
module api_ui {
    class DeckPanel extends Panel {
        private panels;
        private panelShown;
        constructor(idPrefix?: string);
        public getSize(): number;
        public addPanel(panel: Panel): number;
        public getPanel(index: number): Panel;
        public removePanel(index: number): Panel;
        public showPanel(index: number): void;
    }
}
module api_ui {
    class ButtonEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class BodyMask extends DivEl {
        private static instance;
        static get(): BodyMask;
        constructor();
        public activate(): void;
        public deActivate(): void;
    }
}
module api_ui {
    class AbstractButton extends ButtonEl {
        private label;
        constructor(idPrefix: string, label: string);
        public setEnable(value: bool): void;
    }
}
module api_ui_toolbar {
    class Toolbar extends api_ui.DivEl {
        public ext;
        private components;
        constructor();
        private initExt();
        public addAction(action: api_ui.Action): void;
        public addElement(element: api_ui.Element): void;
        public addGreedySpacer(): void;
        private addActionButton(action);
        private hasGreedySpacer();
    }
}
module api_ui_menu {
    class MenuItem extends api_ui.LiEl {
        private action;
        constructor(action: api_ui.Action);
        public setEnable(value: bool): void;
    }
}
module api_ui_detailpanel {
    class DetailPanel extends api_ui.DivEl {
        public ext;
        constructor();
        private initExt();
    }
    class DetailTabPanel extends api_ui.DivEl {
        private model;
        private navigation;
        private tabs;
        private canvas;
        private tabChangeCallback;
        private actionMenu;
        constructor(model: api_model.Model);
        private addHeader(title, subtitle, iconUrl);
        private addCanvas();
        public setTabChangeCallback(callback: (DetailPanelTab: any) => void): void;
        public addTab(tab: DetailPanelTab): void;
        public setActiveTab(tab: DetailPanelTab): void;
        public addAction(action: api_ui.Action): void;
        private createActionMenu();
        private addNavigation();
    }
    class DetailPanelTab {
        public name: string;
        public content: api_ui.Element;
        constructor(name: string);
    }
    class DetailPanelTabList extends api_ui.UlEl {
        private tabs;
        constructor();
        public addTab(tab, clickCallback: (DetailPanelTab: any) => void): void;
        private selectTab(tab);
    }
    class DetailPanelBox extends api_ui.DivEl {
        private model;
        constructor(model: any, removeCallback?: (DetailPanelBox: any) => void);
        private addRemoveButton(callback?);
        private setIcon(iconUrl, size);
        private setData(title, subtitle);
        public getModel(): api_model.Model;
    }
}
module api_ui_wizard {
    class WizardPanel extends api_ui.Panel {
        private steps;
        private stepContainer;
        private wizardStepPanels;
        private titleEl;
        private subTitleEl;
        public ext;
        constructor();
        private initExt();
        public setTitle(title: string): void;
        public setSubtitle(subtitle: string): void;
        public addStep(step: WizardStep): void;
        private addTitle();
        private addSubTitle();
        private addStepContainer();
    }
    class WizardStep {
        private label;
        private panel;
        private active;
        private el;
        constructor(label: string, panel: api_ui.Panel);
        public setEl(el: api_ui.Element): void;
        public setActive(active: bool): void;
        public isActive(): bool;
        public getEl(): api_ui.Element;
        public getLabel(): string;
        public getPanel(): api_ui.Panel;
    }
}
module api_ui_menu {
    class ContextMenu extends api_ui.UlEl {
        private menuItems;
        constructor(...actions: api_ui.Action[]);
        public addAction(action: api_ui.Action): void;
        private createMenuItem(action);
        public showAt(x: number, y: number): void;
        private hide();
        private hideMenuOnOutsideClick(evt);
    }
}
module api_ui_menu {
    class ActionMenu extends api_ui.UlEl {
        private ext;
        private button;
        private menuItems;
        constructor(...actions: api_ui.Action[]);
        public addAction(action: api_ui.Action): void;
        public getExt();
        public showBy(button: ActionMenuButton): void;
        private initExt();
        private createMenuItem(action);
        private hide();
        private hideMenuOnOutsideClick(evt);
    }
    class ActionMenuButton extends api_ui.ButtonEl {
        private ext;
        private menu;
        constructor(menu: ActionMenu);
        public setEnabled(value: bool): void;
        public getExt();
        private initExt();
    }
}
module api_ui_tab {
    interface Tab {
        setTabIndex(value: number);
        getTabIndex(): number;
        getLabel(): string;
    }
}
module api_ui_tab {
    interface TabRemoveListener {
        tabRemove(tab: Tab);
    }
}
module api_ui_tab {
    interface TabSelectedListener {
        selectedTab(tab: Tab);
    }
}
module api_ui_tab {
    interface TabNavigator {
        addTab(tab: Tab);
        getSize(): number;
        addTabSelectedListener(listener: TabSelectedListener);
        addTabRemoveListener(listener: TabRemoveListener);
    }
}
module api_ui_tab {
    class TabMenu extends api_ui.DivEl implements TabNavigator {
        public ext;
        private tabMenuButton;
        private menuEl;
        private showingMenuItems;
        private tabs;
        private tabSelectedListeners;
        private tabRemovedListeners;
        constructor(idPrefix?: string);
        public createTabMenuButton(): TabMenuButton;
        public createMenu(): api_ui.UlEl;
        private initExt();
        private toggleMenu();
        public hideMenu(): void;
        public showMenu(): void;
        public addTab(tab: Tab): void;
        public getSize(): number;
        public removeTab(tab: Tab): void;
        public selectTab(tab: Tab): void;
        public addTabSelectedListener(listener: TabSelectedListener): void;
        public addTabRemoveListener(listener: TabRemoveListener): void;
        public handleTabClickedEvent(tabMenuItem: TabMenuItem): void;
        public handleTabRemoveButtonClickedEvent(tabMenuItem: TabMenuItem): void;
        public fireTabSelected(tab: Tab): void;
        private fireBeforeTabRemoved(tab);
    }
}
module api_ui_tab {
    class TabMenuButton extends api_ui.DivEl {
        private labelEl;
        private tabMenu;
        constructor(idPrefix?: string);
        public setTabMenu(tabMenu: TabMenu): void;
        public setLabel(value: string): void;
    }
}
module api_ui_tab {
    class TabMenuItem extends api_ui.LiEl implements Tab {
        private tabIndex;
        private label;
        private labelEl;
        private tabMenu;
        constructor(label: string);
        public setTabMenu(tabMenu: TabMenu): void;
        public setTabIndex(value: number): void;
        public getTabIndex(): number;
        public getLabel(): string;
    }
}
module api_ui_tab {
    class TabBar extends api_ui.DivEl implements TabNavigator {
        constructor(idPrefix?: string);
        public addTab(tab: Tab): void;
        public getSize(): number;
        public addTabSelectedListener(listener: TabSelectedListener): void;
        public addTabRemoveListener(listener: TabRemoveListener): void;
    }
}
module api_ui_tab {
    class TabPanelController implements TabRemoveListener, TabSelectedListener {
        private tabNavigator;
        private deckPanel;
        private deckIndexByTabIndex;
        constructor(tabNavigator: TabNavigator, deckPanel: api_ui.DeckPanel);
        public addPanel(panel: api_ui.Panel, tab: Tab): void;
        public tabRemove(tab: Tab): void;
        public removeTab(tab: Tab): void;
        public selectedTab(tab: Tab): void;
    }
}
module api_appbar {
    class AppBar extends api_ui.DivEl {
        public ext;
        public appName: string;
        private launcherButton;
        private homeButton;
        private tabMenu;
        private userButton;
        private userInfoPopup;
        constructor(appName);
        private initExt();
        private addLauncherButton();
        private addSeparator();
        private addHomeButton();
        private addTabMenu();
        private addUserButton();
        private addUserInfoPopup();
    }
    class LauncherButton extends api_ui.ButtonEl {
        constructor();
    }
    class Separator extends api_ui.SpanEl {
        constructor();
    }
    class HomeButton extends api_ui.ButtonEl {
        constructor(text: string);
    }
    class TabMenuContainer extends api_ui.DivEl {
        constructor();
    }
    class UserButton extends api_ui.ButtonEl {
        constructor();
        public setIcon(photoUrl: string): void;
    }
}
module api_appbar {
    class UserInfoPopup extends api_ui.DivEl {
        private isShown;
        constructor();
        private createContent();
        private render();
        public toggle(): void;
    }
}
module api_appbar {
    class OpenAppLauncherAction extends api_ui.Action {
        constructor();
    }
    class ShowAppBrowsePanelAction extends api_ui.Action {
        constructor();
    }
    class AppBarActions {
        static OPEN_APP_LAUNCHER: api_ui.Action;
        static SHOW_APP_BROWSER_PANEL: api_ui.Action;
    }
}
module api_appbar {
    class OpenAppLauncherEvent extends api_event.Event {
        constructor();
    }
    class ShowAppBrowsePanelEvent extends api_event.Event {
        constructor();
    }
}
module api_ui_dialog {
    class DialogButton extends api_ui.AbstractButton {
        private action;
        constructor(action: api_ui.Action);
    }
}
module api_ui_dialog {
    interface ModalDialogConfig {
        title: string;
        width: number;
        height: number;
    }
    class ModalDialog extends api_ui.DivEl {
        private config;
        private title;
        private contentPanel;
        private buttonRow;
        constructor(config: ModalDialogConfig);
        public setTitle(value: string): void;
        public appendChildToContentPanel(child: api_ui.Element): void;
        public addAction(action: api_ui.Action): void;
        public show(): void;
        public hide(): void;
        public close(): void;
        public open(): void;
    }
    class ModalDialogTitle extends api_ui.H2El {
        constructor(title: string);
        public setTitle(value: string): void;
    }
    class ModalDialogContentPanel extends api_ui.DivEl {
        constructor();
    }
    class ModalDialogButtonRow extends api_ui.DivEl {
        constructor();
        public addAction(action: api_ui.Action): void;
    }
    class ModalDialogButton extends api_ui.AbstractButton {
        private action;
        constructor(action: api_ui.Action);
    }
    class ModalDialogCancelAction extends api_ui.Action {
        constructor();
    }
}
module api_delete {
    class DeleteItem {
        private iconUrl;
        private displayName;
        constructor(iconUrl: string, displayName: string);
        public getDisplayName(): string;
        public getIconUrl(): string;
    }
}
module api_delete {
    class DeleteDialog extends api_ui_dialog.ModalDialog {
        private modelName;
        private deleteAction;
        private cancelAction;
        private deleteItems;
        private itemList;
        constructor(modelName: string);
        public setDeleteAction(action: api_ui.Action): void;
        public setDeleteItems(deleteItems: DeleteItem[]): void;
    }
    class CancelDeleteDialogAction extends api_ui.Action {
        constructor();
    }
    class DeleteDialogItemList extends api_ui.DivEl {
        constructor();
        public clear(): void;
    }
}
module api_appbar {
    class AppBarTabMenu extends api_ui_tab.TabMenu {
        private tabMenuButton;
        constructor(idPrefix?: string);
        public addTab(tab: api_ui_tab.Tab): void;
        public selectTab(tab: api_ui_tab.Tab): void;
        public createTabMenuButton(): api_ui_tab.TabMenuButton;
    }
}
module api_appbar {
    class AppBarTabMenuButton extends api_ui_tab.TabMenuButton {
        private iconEl;
        private tabCountEl;
        constructor(idPrefix?: string);
        public setTabCount(value: number): void;
    }
    class AppBarTabCount extends api_ui.SpanEl {
        constructor();
        public setCount(value: number): void;
    }
}
module api_appbar {
    class AppBarTabMenuItem extends api_ui_tab.TabMenuItem {
        constructor(label: string);
    }
}
module api {
    class AppBrowsePanel extends api_ui.Panel {
        public ext;
        private browseToolbar;
        private grid;
        private detailPanel;
        private filterPanel;
        constructor(browseToolbar: api_ui_toolbar.Toolbar, grid: any, detailPanel: api_ui_detailpanel.DetailPanel, filterPanel: any);
        private initExt();
    }
}
module api {
    class FormDeckPanel extends api_ui.DeckPanel {
        constructor();
    }
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
        constructor(name: string, handler: Function);
        public getName(): string;
        public getHandler(): Function;
    }
    class Message {
        private type;
        private text;
        private actions;
        constructor(type: Type, text: string);
        public getType(): Type;
        public getText(): string;
        public getActions(): Action[];
        public addAction(name: string, handler: () => void): void;
        public send(): void;
    }
    function newInfo(text: string): Message;
    function newError(text: string): Message;
    function newAction(text: string): Message;
}
module api_notify {
    class NotifyManager {
        private timers;
        private el;
        constructor();
        private render();
        private getWrapperEl();
        public notify(message: Message): void;
        private doNotify(opts);
        private setListeners(el, opts);
        private remove(el);
        private startTimer(el);
        private stopTimer(el);
        private renderNotification(opts);
    }
    function sendNotification(message: Message): void;
}
module api_notify {
    class NotifyOpts {
        public message: string;
        public backgroundColor: string;
        public listeners: Object[];
    }
    function buildOpts(message: Message): NotifyOpts;
}
module api_notify {
    function showFeedback(message: string): void;
    function updateAppTabCount(appId, tabCount: Number): void;
}
module api_content_data {
    class DataId {
        private name;
        private arrayIndex;
        private refString;
        constructor(name: string, arrayIndex: number);
        public getName(): string;
        public getArrayIndex(): number;
        public toString(): string;
        static from(str: string): DataId;
    }
}
module api_content_data {
    class Data {
        private name;
        private arrayIndex;
        private parent;
        constructor(name: string);
        public setArrayIndex(value: number): void;
        public setParent(parent: DataSet): void;
        public getId(): DataId;
        public getName(): string;
        public getParent(): Data;
        public getArrayIndex(): number;
    }
}
module api_content_data {
    class DataSet extends Data {
        private dataById;
        constructor(name: string);
        public nameCount(name: string): number;
        public addData(data: Data): void;
        public getData(dataId: string): Data;
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
        static from(json): Property;
        constructor(name: string, value: string, type: string);
        public getValue(): string;
        public getType(): string;
        public setValue(value: any): void;
    }
}
module api_schema_content_form {
    class FormItem {
        private name;
        constructor(name: string);
        public getName(): string;
    }
}
module api_schema_content_form {
    class InputType {
        private name;
        constructor(json: any);
        public getName(): string;
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
        public getLabel(): string;
        public isImmutable(): bool;
        public getOccurrences(): Occurrences;
        public isIndexed(): bool;
        public getCustomText(): string;
        public getValidationRegex(): string;
        public getHelpText(): string;
    }
}
module api_schema_content_form {
    class Occurrences {
        private minimum;
        private maximum;
        constructor(json);
    }
}
