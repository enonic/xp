module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class InsertMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Insert',
                name: 'insert',
                handler: (event:Event) => {
                    this.onInsert();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onInsert():void {
            /**/
        }

    }
}