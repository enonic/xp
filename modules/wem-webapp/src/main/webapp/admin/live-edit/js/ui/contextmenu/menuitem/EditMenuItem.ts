module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class EditMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        menu = null;

        constructor(menu) {
            super({
                name: 'edit',
                text: 'Edit',
                handler: (event:Event) => {
                    this.onEdit();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onEdit() {
            /* Fire an event in content manager */
        }

    }
}