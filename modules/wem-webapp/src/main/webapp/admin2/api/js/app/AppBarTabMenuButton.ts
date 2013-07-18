module api_app{

    export class AppBarTabMenuButton extends api_ui_tab.TabMenuButton {

        private iconEl:api_dom.ImgEl;

        private tabCountEl:AppBarTabCount;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenuButton");
            this.getEl().addClass("appbar-tabmenu-button");

            this.iconEl = new api_dom.ImgEl();
            this.iconEl.hide();
            this.prependChild(this.iconEl);

            this.tabCountEl = new AppBarTabCount();
            this.prependChild(this.tabCountEl);
        }

        setTabCount(value:number) {
            this.tabCountEl.setCount(value);
        }

        setEditing(value:bool) {
            this.iconEl[value ? "show" : "hide"]();
        }
    }

    export class AppBarTabCount extends api_dom.SpanEl {

        constructor() {
            super();
            this.getEl().addClass("tabcount");
        }

        setCount(value:number) {
            if (value > 0) {
                this.getEl().setInnerHtml("" + value);
            }
            else {
                this.getEl().setInnerHtml("");
            }
        }
    }
}
