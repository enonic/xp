module LiveEdit.ui.contextmenu.menuitem {

    import ItemView = api.liveedit.ItemView;
    import PageComponent = api.content.page.PageComponent;
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

                var selectedPageComponentView = <PageComponentView<PageComponent>> selectedItem;
                selectedPageComponentView.empty();

                new PageComponentResetEvent(selectedPageComponentView).fire();
            }
            else {
                throw new Error("Emptying [" + api.util.getClassName(selectedItem) + "] is not supported");
            }
        }
    }
}