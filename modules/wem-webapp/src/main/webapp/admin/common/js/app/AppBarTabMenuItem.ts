module api.app{

    export class AppBarTabMenuItem extends api.ui.tab.TabMenuItem {

        private tabId:AppBarTabId;

        private editing:boolean;

        constructor(label:string, tabId:AppBarTabId, editing?:boolean) {
            super(label, {removable:true});
            this.editing = editing;
            this.tabId = tabId;

            if (editing) {
                var iconEl = new api.dom.ImgEl();
                this.prependChild(iconEl);
            }
        }

        isEditing():boolean {
            return this.editing;
        }

        getTabId():AppBarTabId {
            return this.tabId;
        }
    }
}
