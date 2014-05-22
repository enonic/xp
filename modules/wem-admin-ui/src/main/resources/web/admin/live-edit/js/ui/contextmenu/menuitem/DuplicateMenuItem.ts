module LiveEdit.ui.contextmenu.menuitem {

    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;

    // Uses
    var $ = $liveEdit;

    export class DuplicateMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Duplicate',
                name: 'duplicate',
                handler: (event: Event) => {
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
            var placeholder = LiveEdit.component.ComponentPlaceholder.fromComponent(component.getType());
            placeholder.getEl().insertAfterEl(component);
            placeholder.init();
            placeholder.showLoadingSpinner();
            new PageComponentDuplicateEvent(component, placeholder).fire();
            LiveEdit.component.Selection.handleSelect(placeholder);
        }
    }
}