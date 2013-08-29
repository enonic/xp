module api_ui_tab {

    export interface TabMenuItemOptions {
        removable?:bool;
        removeText?:string;
    }

    export class TabMenuItem extends api_dom.LiEl implements api_ui.PanelNavigationItem {

        private tabIndex:number;

        private label:string;

        private labelEl:api_dom.SpanEl;

        private tabMenu:TabMenu;

        private visible:bool = true;

        private removable:bool = true;

        private active:bool;

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

        setTabMenu(tabMenu:TabMenu) {
            this.tabMenu = tabMenu;
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

        setLabel(value:string) {
            this.label = value;
            this.labelEl.getEl().setInnerHtml(value);
            this.labelEl.getEl().setAttribute('title', value);

            if (this.tabMenu) {
                this.tabMenu.setButtonLabel(value);
            }
        }

        isVisible():bool {
            return this.visible
        }

        setVisible(value:bool) {
            this.visible = value;
            if (!this.visible) {
                this.remove();
            }
        }

        setActive(value:bool) {
            this.active = value;
            if (this.active) {
                this.getEl().addClass("active");
            } else {
                this.getEl().removeClass("active");
            }
        }

        isRemovable():bool {
            return this.removable;
        }

        setRemovable(value:bool) {
            this.removable = value;
        }

        private remove() {
            if (this.tabMenu) {
                this.tabMenu.removeChild(this);
            }
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
