module LiveEdit.ui.contextmenu.menuitem {

    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;
    import PageComponent = api.content.page.PageComponent;
    import PageComponentView = api.liveedit.PageComponentView;

    export class RemoveMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

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

            var selectedItem = this.menu.selectedComponent;
            if (api.ObjectHelper.iFrameSafeInstanceOf(selectedItem, PageComponentView)) {

                var selectedPageComponent = <PageComponentView<PageComponent>> selectedItem;
                selectedPageComponent.getElement().remove();
                new PageComponentRemoveEvent(selectedPageComponent.getComponentPath()).fire();
            }
            else {
                throw new Error("Removing [" + api.util.getClassName(selectedItem) + "] is not supported");
            }
        }
    }
}