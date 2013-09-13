module api_ui_tab {

    export interface TabMenuItemOptions {
        removable?:boolean;
        removeText?:string;
    }

    export class TabMenuItem extends api_dom.LiEl implements api_ui.PanelNavigationItem {

        private tabIndex:number;

        private label:string;

        private labelEl:api_dom.SpanEl;

        private visible:boolean = true;

        private removable:boolean = true;

        private active:boolean;

        private listeners: TabMenuItemListener[] = [];

        constructor(label:string, options?:TabMenuItemOptions) {
            super("TabMenuItem", "tab-menu-item");
            if (!options) {
                options = {};
            }

            this.labelEl = new api_dom.SpanEl(null, 'label');
            this.appendChild(this.labelEl);
            this.setLabel(label);
            this.labelEl.getEl().addEventListener("click", () => {
                new TabMenuItemSelectEvent(this).fire();
            });

            this.removable = options.removable;
            if (options.removable) {
                var removeButton = new api_dom.ButtonEl();
                removeButton.getEl().setInnerHtml(options.removeText ? options.removeText : "&times;");
                this.prependChild(removeButton);
                removeButton.getEl().addEventListener("click", () => {
                    if (this.removable) {
                        new TabMenuItemCloseEvent(this).fire();
                    }
                });
            }
        }

        getElement(): api_dom.Element {
            return this;
        }

        setIndex(value:number) {
            this.tabIndex = value;
        }

        getIndex():number {
            return this.tabIndex;
        }

        getLabel():string {
            return this.label;
        }

        setLabel(newValue:string) {
            if (this.label == newValue) {
                return;
            }

            var oldValue = this.label;
            this.label = newValue;
            this.labelEl.getEl().setInnerHtml(newValue);
            this.labelEl.getEl().setAttribute('title', newValue);
            this.executeLabelChangedListeners(newValue, oldValue);
        }

        isVisible():boolean {
            return this.visible
        }

        setVisible(value:boolean) {
            this.visible = value;
            if (!this.visible) {
                this.remove();
            }
        }

        setActive(value:boolean) {
            this.active = value;
            if (this.active) {
                this.getEl().addClass("active");
            } else {
                this.getEl().removeClass("active");
            }
        }

        isActive():boolean {
            return this.active;
        }

        setRemovable(value:boolean) {
            this.removable = value;
        }

        isRemovable():boolean {
            return this.removable;
        }

        addListener(listener:TabMenuItemListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:TabMenuItemListener) {
            this.listeners = this.listeners.filter((elem) => {
                return elem != listener;
            });
        }

        private executeLabelChangedListeners(newValue:string, oldValue:string) {
            this.listeners.forEach((listener:TabMenuItemListener) => {
                if (listener.onLabelChanged) {
                    listener.onLabelChanged(newValue, oldValue);
                }
            });
        }

    }

    export class TabMenuItemSelectEvent extends api_event.Event {

        private item:TabMenuItem;

        constructor(item:TabMenuItem) {
            super('tabMenuItemSelect');

            this.item = item;
        }

        getTab():TabMenuItem {
            return this.item;
        }

        static on(handler:(event:TabMenuItemSelectEvent) => void) {
            api_event.onEvent('tabMenuItemSelect', handler);
        }
    }

    export class TabMenuItemCloseEvent extends api_event.Event {

        private item:TabMenuItem;

        constructor(item:TabMenuItem) {
            super('tabMenuItemClose');

            this.item = item;
        }

        getTab():TabMenuItem {
            return this.item;
        }

        static on(handler:(event:TabMenuItemCloseEvent) => void) {
            api_event.onEvent('tabMenuItemClose', handler);
        }
    }
}
