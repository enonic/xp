module LiveEdit.ui.contextmenu.menuitem {

    import ComponentPath = api.content.page.ComponentPath;
    import ItemView = api.liveedit.ItemView;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;

    // Uses
    var $ = $liveEdit;

    export class EmptyMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu: LiveEdit.ui.contextmenu.ContextMenu) {
            super({
                text: 'Empty',
                name: 'clear',
                handler: (event: Event) => {
                    this.onEmptyComponent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onEmptyComponent() {
            var selectedComponent = this.menu.selectedComponent;
            var componentEl: JQuery = selectedComponent.getElement();
            var component = ItemView.fromJQuery(componentEl, false);

            selectedComponent.deselect();
            new PageComponentResetEvent(selectedComponent.getComponentPath()).fire();

            var emptyComponent = LiveEdit.component.ComponentPlaceholder.fromComponent(selectedComponent.getType());
            emptyComponent.setComponentPath(component.getComponentPath());

            componentEl.replaceWith(emptyComponent.getHTMLElement());
            emptyComponent.init();

            LiveEdit.component.Selection.handleSelect(emptyComponent);
        }
    }
}