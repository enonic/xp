module LiveEdit.ui.contextmenu.menuitem {

    export class DetailsMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        menu = null;

        constructor(menu) {
            super({
                text: 'Show Details',
                name: 'details',
                handler: (event:Event) => {
                    this.onShowDetails();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onShowDetails():void {
            /**/
        }

    }
}