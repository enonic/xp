module api.app{

    export class AppBarTabMenuButton extends api.ui.tab.TabMenuButton {

        private iconEl:api.dom.ImgEl;

        private tabCountEl:AppBarTabCount;

        constructor() {
            super();

            this.tabCountEl = new AppBarTabCount();
            this.prependChild(this.tabCountEl);

            this.iconEl = new api.dom.ImgEl();
            this.prependChild(this.iconEl);
        }

        setTabCount(value:number) {
            this.tabCountEl.setCount(value);
        }

        setEditing(editing:boolean) {
            if (editing && !this.hasClass('editing')) {
                this.addClass('editing');
            } else if (!editing && this.hasClass('editing')) {
                this.removeClass('editing');
            }
        }
    }

    export class AppBarTabCount extends api.dom.SpanEl {

        constructor() {
            super("tab-count");
        }

        setCount(value:number) {
            this.getEl().setInnerHtml(value > 0 ? "" + value : "");
        }
    }
}
