module api_ui_tab {

    export interface TabBarItemOptions {
        removable?:boolean;
    }

    export class TabBarItem extends api_dom.LiEl implements api_ui.PanelNavigationItem {

        private label: string;

        private index: number;

        private active:boolean = false;

        private visible:boolean = true;

        private removable:boolean = true;

        constructor(label:string, options:TabBarItemOptions = {}) {
            super("TabBarItem", "tab-bar-item");

            this.setLabel(label);
            this.removable = options.removable;
        }

        getElement():api_dom.Element {
            return this;
        }

        setIndex(value:number) {
            this.index = value;
        }

        getIndex():number {
            return this.index;
        }

        setLabel(value:string) {
            this.label = value;
            this.getEl().setInnerHtml(value);
            this.getEl().setAttribute('title', value);
        }

        getLabel():string {
            return this.label;
        }

        setVisible(value) {
            this.visible = value;
            if (!this.visible) {
                this.hide();
            }
        }

        isVisible():boolean {
            return this.visible;
        }

        setActive(value:boolean) {
            this.active = value;
            this.active ? this.addClass("active") : this.removeClass("active");
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

    }

}