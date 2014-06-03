module LiveEdit.ui.contextmenu.menuitem {

    import ItemType = api.liveedit.ItemType;
    import ItemView = api.liveedit.ItemView;

    export class ParentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Select Parent',
                name: 'parent',
                handler: (event: Event) => {
                    this.onSelectParent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onSelectParent() {
            var parentElement: JQuery = this.menu.selectedComponent.getElement().parents('[data-live-edit-type]');

            if (parentElement && parentElement.length > 0) {
                this.menu.selectedComponent.deselect();
                var parentComponent = pageItemViews.getItemViewByElement($(parentElement[0]).get(0));
                LiveEdit.component.Selection.handleSelect(parentComponent);

                var xPos = parentComponent.getElement().offset().left + parentComponent.getElement().width() / 2 -
                           (this.menu.getEl().width() / 2);
                var yPos = parentComponent.getElement().offset().top + 11;
                this.menu.moveToXY(xPos, yPos);

                this.scrollComponentIntoView(parentComponent);
            }
        }

        private scrollComponentIntoView(component: ItemView): void {
            var dimensions = component.getElementDimensions();
            if (dimensions.top <= window.pageYOffset) {
                wemjq('html, body').animate({scrollTop: dimensions.top - 10}, 200);
            }
        }
    }
}