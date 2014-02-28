module api.ui.tab {

    export interface TabMenuItemOptions {
        removable?:boolean;
        removeText?:string;
    }

    export class TabMenuItem extends api.dom.LiEl implements api.ui.PanelNavigationItem {

        private tabIndex: number;

        private label: string;

        private labelEl: api.dom.SpanEl;

        private visibleInMenu: boolean = true;

        private removable: boolean = true;

        private active: boolean;

        private labelChangedListeners: {(event: TabMenuItemLabelChangedEvent):void}[] = [];

        private closedListeners: {(event: TabMenuItemClosedEvent):void}[] = [];

        private selectedListeners: {(event: TabMenuItemSelectedEvent):void}[] = [];

        constructor(label: string, options?: TabMenuItemOptions) {
            super("tab-menu-item");
            if (!options) {
                options = {};
            }

            this.labelEl = new api.dom.SpanEl('label');
            this.appendChild(this.labelEl);
            this.setLabel(label);
            this.labelEl.getEl().addEventListener("click", () => {
                this.notifySelectedListeners(this);
            });

            this.removable = options.removable;
            if (options.removable) {
                var removeButton = new api.dom.ButtonEl();
                removeButton.getEl().setInnerHtml(options.removeText ? options.removeText : "&times;");
                this.prependChild(removeButton);
                removeButton.getEl().addEventListener("click", () => {
                    if (this.removable) {
                        this.notifyClosedListeners(this);
                    }
                });
            }
        }

        setIndex(value: number) {
            this.tabIndex = value;
        }

        getIndex(): number {
            return this.tabIndex;
        }

        getLabel(): string {
            return this.label;
        }

        setLabel(newValue: string) {
            if (this.label == newValue) {
                return;
            }

            var oldValue = this.label;
            this.label = newValue;
            this.labelEl.getEl().setInnerHtml(newValue);
            this.labelEl.getEl().setAttribute('title', newValue);
            this.notifyLabelChangedListeners(newValue, oldValue);
        }

        isVisibleInMenu(): boolean {
            return this.visibleInMenu
        }

        setVisibleInMenu(value: boolean) {
            this.visibleInMenu = value;
            super.setVisible(value);
        }

        setActive(value: boolean) {
            this.active = value;
            if (this.active) {
                this.getEl().addClass("active");
            } else {
                this.getEl().removeClass("active");
            }
        }

        isActive(): boolean {
            return this.active;
        }

        setRemovable(value: boolean) {
            this.removable = value;
        }

        isRemovable(): boolean {
            return this.removable;
        }

        onLabelChanged(listener: (event: TabMenuItemLabelChangedEvent)=>void) {
            this.labelChangedListeners.push(listener);
        }

        onSelected(listener: (event: TabMenuItemSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        onClosed(listener: (event: TabMenuItemClosedEvent)=>void) {
            this.closedListeners.push(listener);
        }

        unLabelChanged(listener: (event: TabMenuItemLabelChangedEvent)=>void) {
            this.labelChangedListeners =
            this.labelChangedListeners.filter((currentListener: (event: TabMenuItemLabelChangedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unSelected(listener: (event: TabMenuItemSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: TabMenuItemSelectedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        unClosed(listener: (event: TabMenuItemClosedEvent)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: TabMenuItemClosedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyLabelChangedListeners(newValue: string, oldValue: string) {
            this.labelChangedListeners.forEach((listener: (event: TabMenuItemLabelChangedEvent)=>void) => {
                listener.call(this, new TabMenuItemLabelChangedEvent(this, oldValue, newValue));
            });
        }

        private notifySelectedListeners(tab: TabMenuItem) {
            this.selectedListeners.forEach((listener: (event: TabMenuItemSelectedEvent)=>void) => {
                listener.call(this, new TabMenuItemSelectedEvent(tab));
            });
        }

        private notifyClosedListeners(tab: TabMenuItem) {
            this.closedListeners.forEach((listener: (event: TabMenuItemClosedEvent)=>void) => {
                listener.call(this, new TabMenuItemClosedEvent(tab));
            });
        }

    }

}
