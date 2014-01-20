module api.app{

    export class AppBarTabMenuButton extends api.ui.tab.TabMenuButton {

        private iconEl:api.dom.ImgEl;

        private tabCountEl:AppBarTabCount;

        constructor() {
            super();
            this.getEl().addClass("appbar-tabmenu-button");

            this.iconEl = new api.dom.ImgEl();
            this.iconEl.hide();
            this.prependChild(this.iconEl);

            this.tabCountEl = new AppBarTabCount();
            this.appendChild(this.tabCountEl);
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
            if (value > 0) {
                this.getEl().setInnerHtml("" + value);
            }
            else {
                this.getEl().setInnerHtml("");
            }
        }
    }
}
