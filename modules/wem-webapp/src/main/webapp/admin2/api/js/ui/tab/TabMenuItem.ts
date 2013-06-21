module api_ui_tab {

    export class TabMenuItem extends api_dom.LiEl implements Tab {

        private tabIndex:number;

        private label:string;

        private labelEl:api_dom.SpanEl;

        private tabMenu:TabMenu;

        private visible:bool = true;

        private removable:bool = true;

        constructor(label:string) {
            super("TabMenuItem", "tab-menu-item");

            this.label = label;
            this.labelEl = new api_dom.SpanEl();
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);

            var removeButton = new api_dom.ButtonEl();
            removeButton.getEl().setInnerHtml("&times;");
            this.appendChild(removeButton);

            this.labelEl.getEl().addEventListener("click", () => {
                this.tabMenu.handleTabClickedEvent(this);
            });

            removeButton.getEl().addEventListener("click", () => {
                if (this.removable) {
                    this.tabMenu.handleTabRemoveButtonClickedEvent(this);
                    if (this.tabMenu.getSize() == 0) {
                        this.tabMenu.hideMenu();
                    }
                }
            });
        }

        setTabMenu(tabMenu:TabMenu) {
            this.tabMenu = tabMenu;
        }

        setTabIndex(value:number) {
            this.tabIndex = value;
        }

        getTabIndex():number {
            return this.tabIndex;
        }

        getLabel():string {
            return this.label;
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
}
