module LiveEdit.ui.contextmenu.menuitem {

    import PageComponentDuplicateEvent = api.liveedit.PageComponentDuplicateEvent;
    import PageComponentView = api.liveedit.PageComponentView;

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

            var selectedItem = this.menu.selectedComponent;

            if (api.ObjectHelper.iFrameSafeInstanceOf(selectedItem, PageComponentView)) {

                var selectedPageComponent = <PageComponentView> selectedItem;

                var placeholder = LiveEdit.component.ComponentPlaceholder.fromComponent(selectedPageComponent.getType());
                placeholder.getEl().insertAfterEl(selectedPageComponent);
                placeholder.init();
                placeholder.showLoadingSpinner();
                new PageComponentDuplicateEvent(selectedPageComponent, placeholder).fire();
                LiveEdit.component.Selection.handleSelect(placeholder);
            }
            else {
                throw new Error("Duplicating [" + api.util.getClassName(selectedItem) + "] is not supported");
            }
        }
    }
}