module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ParentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        menu = null;

        constructor(menu) {
            super({
                text: 'Select Parent',
                name: 'parent',
                handler: (event:Event) => {
                    this.onSelectParent();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onSelectParent() {
            var parentElement:JQuery = this.menu.selectedComponent.getElement().parents('[data-live-edit-type]');

            if (parentElement && parentElement.length > 0) {
                var parentComponent = new LiveEdit.component.Component($(parentElement[0]));

                LiveEdit.Selection.deSelect();
                LiveEdit.Selection.select(parentComponent);

                this.scrollComponentIntoView(parentComponent);
            }
        }

        private scrollComponentIntoView(component:LiveEdit.component.Component):void {
            var dimensions:ElementDimensions = component.getElementDimensions();
            if (dimensions.top <= window.pageYOffset) {
                $('html, body').animate({scrollTop: dimensions.top - 10}, 200);
            }
        }
    }
}