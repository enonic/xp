module LiveEdit.ui.contextmenu.menuitem {

    import ComponentPath = api.content.page.ComponentPath;
    import ItemView = api.liveedit.ItemView;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageComponentResetEvent = api.liveedit.PageComponentResetEvent;

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

            var selectedItem = this.menu.selectedComponent;
            if (api.ObjectHelper.iFrameSafeInstanceOf(selectedItem, PageComponentView)) {

                var selectedPageComponent = <PageComponentView> selectedItem;

                var componentEl: JQuery = selectedPageComponent.getElement();
                var component = PageComponentView.fromJQuery(componentEl, false);

                selectedPageComponent.deselect();
                new PageComponentResetEvent(selectedPageComponent.getComponentPath()).fire();

                var emptyComponent = LiveEdit.component.ComponentPlaceholder.fromComponent(selectedPageComponent.getType());
                emptyComponent.setComponentPath(component.getComponentPath());

                componentEl.replaceWith(emptyComponent.getHTMLElement());
                emptyComponent.init();

                LiveEdit.component.Selection.handleSelect(emptyComponent);
            }
            else {
                throw new Error("Emptying [" + api.util.getClassName(selectedItem) + "] is not supported");
            }
        }
    }
}