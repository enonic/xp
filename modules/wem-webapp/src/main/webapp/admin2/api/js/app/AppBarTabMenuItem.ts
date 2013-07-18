module api_app{

    export class AppBarTabMenuItem extends api_ui_tab.TabMenuItem {

        private editing:bool;

        constructor(label:string, editing?:bool) {
            super(label);
            this.editing = editing;

            if (editing) {
                var iconEl = new api_dom.ImgEl();
                this.prependChild(iconEl);
            }
        }

        isEditing():bool {
            return this.editing;
        }

    }

}
