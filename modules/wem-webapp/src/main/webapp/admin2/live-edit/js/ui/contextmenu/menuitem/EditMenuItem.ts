module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class EditMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var button:JQuery = this.createButton({
                name: 'edit',
                text: 'Edit',
                handler: (event) => {
                    event.stopPropagation();

                    var paragraphComponent = this.menu.selectedComponent;
                    if (paragraphComponent && paragraphComponent.getElement().length > 0) {
                        $(window).trigger('editParagraphComponent.liveEdit', [paragraphComponent]);
                    }
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }
    }
}