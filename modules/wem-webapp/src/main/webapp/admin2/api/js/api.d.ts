module api_util {
    class ImageLoader {
        private static images;
        static get(url: string, width?: number, height?: number);
    }
}
module api_util {
    var baseUri: string;
    function getAbsoluteUri(uri: string): string;
}
module api_handler {
    interface DeleteSpaceParam {
        spaceName: string[];
    }
}
module api_handler {
    class DeleteSpacesHandler {
        public doDelete(deleteSpaceParam: DeleteSpaceParam, callback: (thisArg: any, success: any, result: any) => void): void;
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
module api_action {
    class Action {
        private label;
        private enabled;
        private executionListeners;
        private propertyChangeListeners;
        constructor(label: string);
        public getLabel(): string;
        public setLabel(value: string): void;
        public isEnabled(): bool;
        public setEnabled(value: bool): void;
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
        public addClass(clsName: string): void;
        public hasClass(clsName: string): bool;
        public removeClass(clsName: string): void;
        public addEventListener(eventName: string, f: (event: Event) => any): void;
        public appendChild(child: HTMLElement): void;
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
        constructor(elementName: string, name?: string, elHelper?: ElementHelper);
        public getId(): string;
        public getEl(): ElementHelper;
        public getHTMLElement(): HTMLElement;
        public appendChild(child: Element): void;
        public removeChildren(): void;
    }
}
module api_ui {
    class DivEl extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class H1El extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class H2El extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class H3El extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class H4El extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class UlEl extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class LiEl extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class EmEl extends Element {
        constructor(name?: string);
    }
}
module api_ui {
    class ImgEl extends Element {
        constructor(name?: string);
        public getEl(): ImgHelper;
    }
}
module api_ui {
    class ButtonEl extends Element {
        constructor(name?: string);
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
        constructor(name: string, label: string);
        public setEnable(value: bool): void;
    }
}
module api_ui_toolbar {
    class Toolbar extends api_ui.DivEl {
        public ext;
        private components;
        constructor();
        private initExt();
        public addAction(action: api_action.Action): void;
        public addGreedySpacer(): void;
        private doAddAction(action);
        private hasGreedySpacer();
    }
}
module api_ui_menu {
    class MenuItem extends api_ui.LiEl {
        private action;
        constructor(action: api_action.Action);
        public setEnable(value: bool): void;
    }
}
module api_ui_detailpanel {
    class DetailPanel extends api_ui.DivEl {
        public ext;
        constructor();
        private initExt();
    }
    class DetailPanelBox extends api_ui.DivEl {
        private model;
        constructor(model: any, event?: api_event.Event);
        private addRemoveButton(removeEvent?);
        private setIcon(iconUrl, size);
        private setData(title, subtitle);
    }
}
module api_ui_menu {
    class ContextMenu extends api_ui.UlEl {
        private menuItems;
        constructor();
        public addAction(action: api_action.Action): void;
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
        constructor(...actions: api_action.Action[]);
        public addAction(action: api_action.Action): void;
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
module api_ui_dialog {
    class DialogButton extends api_ui.AbstractButton {
        private action;
        constructor(action: api_action.Action);
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
        public addAction(action: api_action.Action): void;
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
        public addAction(action: api_action.Action): void;
    }
    class ModalDialogButton extends api_ui.AbstractButton {
        private action;
        constructor(action: api_action.Action);
    }
    class ModalDialogCancelAction extends api_action.Action {
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
        public setDeleteAction(action: api_action.Action): void;
        public setDeleteItems(deleteItems: DeleteItem[]): void;
    }
    class CancelDeleteDialogAction extends api_action.Action {
        constructor();
    }
    class DeleteDialogItemList extends api_ui.DivEl {
        constructor();
        public clear(): void;
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
