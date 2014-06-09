module LiveEdit.ui.contextmenu.menuitem {

    import PageComponent = api.content.page.PageComponent;
    import ItemType = api.liveedit.ItemType;
    import ItemView = api.liveedit.ItemView;
    import PageView = api.liveedit.PageView;
    import RegionView = api.liveedit.RegionView;
    import PageComponentView = api.liveedit.PageComponentView;
    import ContentView = api.liveedit.ContentView;

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

            var parentView: ItemView = this.menu.selectedComponent.getParentItemView();
            if (parentView) {
                this.menu.selectedComponent.deselect();
                parentView.select();

                var parentEl = parentView.getElement();
                var xPos = parentEl.offset().left + parentEl.width() / 2 - (this.menu.getEl().width() / 2);
                var yPos = parentEl.offset().top + 11;
                this.menu.moveToXY(xPos, yPos);

                this.scrollComponentIntoView(parentView);
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