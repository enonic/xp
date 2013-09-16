module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class RemoveMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        menu = null;

        constructor(menu) {
            super({
                text: 'Remove',
                name: 'remove',
                handler: (event:Event) => {
                    // For demo purposes
                    this.onRemoveComponent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onRemoveComponent() {
            this.menu.selectedComponent.getElement().remove();
            $(window).trigger('componentRemoved.liveEdit');
        }
    }
}