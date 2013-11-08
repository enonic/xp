module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ViewMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'View',
                name: 'view',
                handler: (event:Event) => {
                    this.onView();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onView() {
            /**/
        }

    }
}