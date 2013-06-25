module api_app{

    export class AppBarTabMenuButton extends api_ui_tab.TabMenuButton {

        private iconEl:api_dom.SpanEl;

        private tabCountEl:AppBarTabCount;

        constructor(idPrefix?:string) {
            super(idPrefix || "AppBarTabMenuButton");
            this.getEl().addClass("appbar-tabmenu-button");

            this.iconEl = new api_dom.SpanEl(); // TODO:
            this.iconEl.getEl().addClass("icon-icomoon-pencil-32");
            this.prependChild(this.iconEl);

            this.tabCountEl = new AppBarTabCount;
            this.appendChild(this.tabCountEl);
        }

        setTabCount(value:number) {
            this.tabCountEl.setCount(value);
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
