module api.app{

    export class AppBarTabMenuButton extends api.ui.tab.TabMenuButton {

        private iconEl:api.dom.ImgEl;

        private tabCountEl:AppBarTabCount;

        constructor() {
            super();
            this.getEl().addClass("appbar-tabmenu-button");

            this.tabCountEl = new AppBarTabCount();
            this.prependChild(this.tabCountEl);

            this.iconEl = new api.dom.ImgEl();
            this.iconEl.hide();
            this.prependChild(this.iconEl);
        }

        setTabCount(value:number) {
            this.tabCountEl.setCount(value);
        }

        setEditing(value:boolean) {
            this.iconEl[value ? "show" : "hide"]();
        }
    }

    export class AppBarTabCount extends api.dom.SpanEl {

        constructor() {
            super();
            this.getEl().addClass("tabcount");
        }

        setCount(value:number) {
            this.getEl().setInnerHtml(value > 0 ? "" + value : "");
        }
    }
}
