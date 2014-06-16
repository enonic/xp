module LiveEdit.ui.contextmenu.menuitem {

    import PageComponent = api.content.page.PageComponent;
    import PageComponentView = api.liveedit.PageComponentView;

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

                var selectedPageComponentView = <PageComponentView<PageComponent>> selectedItem;

                LiveEdit.LiveEditPage.get().duplicatePageComponent(selectedPageComponentView);
            }
            else {
                throw new Error("Duplicating [" + api.util.getClassName(selectedItem) + "] is not supported");
            }
        }
    }
}