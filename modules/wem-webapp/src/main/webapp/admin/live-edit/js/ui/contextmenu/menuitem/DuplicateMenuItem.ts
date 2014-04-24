module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class DuplicateMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Duplicate',
                name: 'duplicate',
                handler: (event:Event) => {
                    this.onDuplicateComponent();
                    event.preventDefault();
                    event.stopPropagation();
                    return false;
                }
            }, menu);

            this.menu = menu;
        }

        private onDuplicateComponent() {
            var component = this.menu.selectedComponent;
            var placeholder = LiveEdit.component.ComponentPlaceholder.fromComponent(LiveEdit.component.Type[component.getComponentType().getName().toUpperCase()]);
            placeholder.getEl().insertAfterEl(component);
            placeholder.init();
            placeholder.showLoadingSpinner();
            $(window).trigger('componentDuplicated.liveEdit', [component, placeholder]);
            LiveEdit.component.Selection.handleSelect(placeholder.getHTMLElement());
        }
    }
}