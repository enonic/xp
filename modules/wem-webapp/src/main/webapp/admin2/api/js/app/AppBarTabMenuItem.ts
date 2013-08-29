module api_app{

    export class AppBarTabMenuItem extends api_ui_tab.TabMenuItem {

        private itemId: string;
        private editing:bool;

        constructor(label:string, itemId:string, editing?:bool) {
            super(label, {removable:true});
            this.editing = editing;
            this.itemId = itemId;

            if (editing) {
                var iconEl = new api_dom.ImgEl();
                this.prependChild(iconEl);
            }
        }

        isEditing():bool {
            return this.editing;
        }

        getItemId() {
            return this.itemId;
        }

    }

}
