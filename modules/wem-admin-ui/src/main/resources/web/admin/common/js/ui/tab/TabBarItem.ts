module api.ui.tab {

    export interface TabBarItemOptions {
        removable?:boolean;
    }

    export class TabBarItem extends api.dom.LiEl implements api.ui.NavigationItem {

        private label: string;

        private index: number;

        private active: boolean = false;

        private removable: boolean = true;

        private onSelectedListeners: {(event: TabBarItemEvent):void}[] = [];

        constructor(label: string, options: TabBarItemOptions = {}) {
            super("tab-bar-item");

            this.setLabel(label);
            this.removable = options.removable;

            this.onClicked((event: MouseEvent) => {
                this.notifySelectedListeners();
            });
        }

        setIndex(value: number) {
            this.index = value;
        }

        getIndex(): number {
            return this.index;
        }

        setLabel(value: string) {
            this.label = value;
            this.getEl().setInnerHtml(value);
            this.getEl().setAttribute('title', value);
        }

        getLabel(): string {
            return this.label;
        }

        setActive(value: boolean) {
            this.active = value;
            this.active ? this.addClass("active") : this.removeClass("active");
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

        onSelected(listener: (event: TabBarItemEvent)=>void) {
            this.onSelectedListeners.push(listener);
        }

        unSelected(listener: (event: TabBarItemEvent)=>void) {
            this.onSelectedListeners = this.onSelectedListeners.filter((currentListener: (event: TabBarItemEvent)=>void) => {
                return currentListener != listener;
            });
        }

        private notifySelectedListeners() {
            this.onSelectedListeners.forEach((listener: (event: TabBarItemEvent)=>void) => {
                listener.call(this, new TabBarItemEvent(this));
            });
        }

    }

}