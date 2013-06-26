module api_ui {
    class KeyBindings {
        private static mousetraps;
        private static shelves;
        static bindKeys(bindings: KeyBinding[]): void;
        static bindKey(binding: KeyBinding): void;
        static unbindKeys(bindings: KeyBinding[]): void;
        static unbindKey(binding: KeyBinding): void;
        static trigger(combination: string, action?: string): void;
        static reset(): void;
        static shelveBindings(): void;
        static unshelveBindings(): void;
    }
    class KeyBinding {
        private combination;
        private callback;
        private action;
        constructor(combination: string, callback?: (e: ExtendedKeyboardEvent, combo: string) => any, action?: string);
        public setCallback(value: (e: ExtendedKeyboardEvent, combo: string) => any): KeyBinding;
        public setAction(value: string): KeyBinding;
        public getCombination(): string;
        public getCallback(): (e: ExtendedKeyboardEvent, combo: string) => any;
        public getAction(): string;
        static newKeyBinding(combination: string): KeyBinding;
    }
}
module api_ui {
    class Action {
        private label;
        private iconClass;
        private shortcut;
        private enabled;
        private executionListeners;
        private propertyChangeListeners;
        constructor(label: string, shortcut?: string);
        public getLabel(): string;
        public setLabel(value: string): void;
        public isEnabled(): bool;
        public setEnabled(value: bool): void;
        public getIconClass(): string;
        public setIconClass(value: string): void;
        public hasShortcut(): bool;
        public getShortcut(): KeyBinding;
        public execute(): void;
        public addExecutionListener(listener: (action: Action) => void): void;
        public addPropertyChangeListener(listener: (action: Action) => void): void;
        static getKeyBindings(actions: Action[]): KeyBinding[];
    }
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
module api_content_data {
    class DataSet extends Data {
        private dataById;
        constructor(name: string);
        public nameCount(name: string): number;
        public addData(data: Data): void;
        public getData(dataId: string): Data;
    }
}
module api_dom {
    class ElementHelper {
        private el;
        static fromName(name: string): ElementHelper;
        constructor(element: HTMLElement);
        public getHTMLElement(): HTMLElement;
        public insertBefore(newEl: Element, existingEl: Element): void;
        public insertBeforeEl(existingEl: Element): void;
        public insertAfterEl(existingEl: Element): void;
        public setDisabled(value: bool): ElementHelper;
        public isDisabled(): bool;
        public setId(value: string): ElementHelper;
        public setInnerHtml(value: string): ElementHelper;
        public setValue(value: string): ElementHelper;
        public addClass(clsName: string): ElementHelper;
        public setClass(value: string): ElementHelper;
        public hasClass(clsName: string): bool;
        public removeClass(clsName: string): ElementHelper;
        public addEventListener(eventName: string, f: (event: Event) => any): ElementHelper;
        public removeEventListener(eventName: string, f: (event: Event) => any): ElementHelper;
        public appendChild(child: HTMLElement): ElementHelper;
        public setData(name: string, value: string): ElementHelper;
        public getData(name: string): string;
        public getDisplay(): string;
        public setDisplay(value: string): ElementHelper;
        public getVisibility(): string;
        public setVisibility(value: string): ElementHelper;
        public setPosition(value: string): ElementHelper;
        public setWidth(value: string): ElementHelper;
        public getWidth(): number;
        public setHeight(value: string): ElementHelper;
        public getHeight(): number;
        public setTop(value: string): ElementHelper;
        public setLeft(value: string): ElementHelper;
        public setMarginLeft(value: string): ElementHelper;
        public setMarginRight(value: string): ElementHelper;
        public setMarginTop(value: string): ElementHelper;
        public setMarginBottom(value: string): ElementHelper;
        public setZindex(value: number): ElementHelper;
        public setBackgroundImage(value: string): ElementHelper;
        public remove(): void;
        public getOffset(): {
            top: number;
            left: number;
        };
    }
}
module api_dom {
    class Element {
        private static constructorCounter;
        private el;
        private id;
        constructor(elementName: string, idPrefix?: string, className?: string, elHelper?: ElementHelper);
        public show(): void;
        public hide(): void;
        public isVisible(): bool;
        public empty(): void;
        public getId(): string;
        public getEl(): ElementHelper;
        public getHTMLElement(): HTMLElement;
        public appendChild(child: Element): void;
        public prependChild(child: Element): void;
        public removeChild(child: Element): void;
        public insertAfterEl(existingEl: Element): void;
        public insertBeforeEl(existingEl: Element): void;
        public removeChildren(): void;
        public remove(): void;
    }
}
module api_dom {
    class DivEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_dom {
    class ButtonEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_dom {
    class SpanEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_dom {
    class UlEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_dom {
    class LiEl extends Element {
        constructor(idPrefix?: string, className?: string);
    }
}
module api_ui {
    class Panel extends api_dom.DivEl {
        constructor(idPrefix?: string);
    }
}
module api_ui {
    class DeckPanel extends Panel {
        private panels;
        private panelShown;
        constructor(idPrefix?: string);
        public isEmpty(): bool;
        public getSize(): number;
        public addPanel(panel: Panel): number;
        public getPanel(index: number): Panel;
        public getLastPanel(): Panel;
        public getPanelShown(): Panel;
        public getPanelShownIndex(): number;
        public getPanelIndex(panel: Panel): number;
        public removePanel(panelToRemove: Panel, checkCanRemovePanel?: bool): number;
        public removePanelByIndex(index: number, checkCanRemovePanel?: bool): Panel;
        public canRemovePanel(panel: Panel, index: number): bool;
        private doRemovePanel(panelToRemove, index, checkCanRemovePanel);
        private isShownPanel(panelIndex);
        public showPanel(index: number): void;
    }
}
module api_ui {
    interface PanelNavigationItem {
        setIndex(value: number);
        getIndex(): number;
        getLabel(): string;
        isVisible(): bool;
        isRemovable(): bool;
    }
}
module api_ui {
    interface DeckPanelNavigator {
        addNavigationItem(item: PanelNavigationItem);
        removeNavigationItem(item: PanelNavigationItem);
        getNavigationItem(index: number);
        selectNavigationItem(index: number);
        getSelectedNavigationItem(): PanelNavigationItem;
        deselectNavigationItem();
        getSize(): number;
        addNavigationItemSelectedListener(listener: (item: PanelNavigationItem) => void);
        addNavigationItemRemoveListener(listener: (item: PanelNavigationItem) => bool);
    }
}
module api_ui_tab {
    class TabMenuItem extends api_dom.LiEl implements api_ui.PanelNavigationItem {
        private tabIndex;
        private label;
        private labelEl;
        private tabMenu;
        private visible;
        private removable;
        private active;
        constructor(label: string);
        public setTabMenu(tabMenu: TabMenu): void;
        public setIndex(value: number): void;
        public getIndex(): number;
        public getLabel(): string;
        public isVisible(): bool;
        public setVisible(value: bool): void;
        public setActive(value: bool): void;
        public isRemovable(): bool;
        public setRemovable(value: bool): void;
        private remove();
    }
}
module api_ui_tab {
    class TabMenuButton extends api_dom.DivEl {
        private labelEl;
        private tabMenu;
        constructor(idPrefix?: string);
        public setTabMenu(tabMenu: TabMenu): void;
        public setLabel(value: string): void;
    }
}
module api_ui_tab {
    class TabMenu extends api_dom.DivEl implements api_ui.DeckPanelNavigator {
        public ext;
        private tabMenuButton;
        private menuEl;
        private showingMenuItems;
        private tabs;
        private selectedTab;
        private tabSelectedListeners;
        private tabRemovedListeners;
        constructor(idPrefix?: string);
        public createTabMenuButton(): TabMenuButton;
        public createMenu(): api_dom.UlEl;
        private initExt();
        private toggleMenu();
        public hideMenu(): void;
        public showMenu(): void;
        public addNavigationItem(tab: api_ui.PanelNavigationItem): void;
        public isEmpty(): bool;
        public getSize(): number;
        public countVisible(): number;
        public getSelectedTabIndex(): number;
        public getSelectedNavigationItem(): api_ui.PanelNavigationItem;
        public getNavigationItem(tabIndex: number): TabMenuItem;
        public removeNavigationItem(tab: api_ui.PanelNavigationItem): void;
        private isSelectedTab(tab);
        private isLastTab(tab);
        private updateActiveTab(tabIndex);
        public selectNavigationItem(tabIndex: number): void;
        public deselectNavigationItem(): void;
        public addNavigationItemSelectedListener(listener: (tab: api_ui.PanelNavigationItem) => void): void;
        public addNavigationItemRemoveListener(listener: (tab: api_ui.PanelNavigationItem) => bool): void;
        public handleTabClickedEvent(tabMenuItem: TabMenuItem): void;
        public handleTabRemoveButtonClickedEvent(tabMenuItem: TabMenuItem): void;
        public fireTabSelected(tab: api_ui.PanelNavigationItem): void;
        private fireTabRemoveEvent(tab);
    }
}
