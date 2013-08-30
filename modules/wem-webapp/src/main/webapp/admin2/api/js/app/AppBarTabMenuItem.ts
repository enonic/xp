module api_app{

    export class AppBarTabMenuItem extends api_ui_tab.TabMenuItem {

        private itemId: string;
        private editing:boolean;

        constructor(label:string, itemId:string, editing?:boolean) {
            super(label, {removable:true});
            this.editing = editing;
            this.itemId = itemId;

            if (editing) {
                var iconEl = new api_dom.ImgEl();
                this.prependChild(iconEl);
            }
        }

        isEditing():boolean {
            return this.editing;
        }

        getItemId() {
            return this.itemId;
        }

    }

}
