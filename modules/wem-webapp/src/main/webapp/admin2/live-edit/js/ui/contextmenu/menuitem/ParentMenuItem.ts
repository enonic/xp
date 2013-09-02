module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ParentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;

            this.init();
        }

        init():void {
            var $button = this.createButton({
                id: 'live-edit-button-parent',
                text: 'Select Parent',
                handler: (event) => {
                    event.stopPropagation();

                    // Fixme: extract to method
                    var parentElement:JQuery = this.menu.selectedComponent.getElement().parents('[data-live-edit-type]');

                    if (parentElement && parentElement.length > 0) {
                        var parentComponent = new LiveEdit.component.Component($(parentElement[0]));

                        var menuPagePosition:any = {x: 0, y: 0};
                        $(window).trigger('selectComponent.liveEdit', [parentComponent, menuPagePosition]);

                        this.scrollComponentIntoView(parentComponent);

                        // Force position of the menu after component is selected.
                        // We could move this code to menu show.
                        // The position needs to be updated after menu is updated with info in order to get the right dimensions (width) of the menu.
                        var menuWidth = this.menu.getEl().outerWidth();
                        var dimensions:ElementDimensions = parentComponent.getElementDimensions(),
                            newMenuPosition = {x: dimensions.left + (dimensions.width / 2) - (menuWidth / 2), y: dimensions.top + 10};

                        this.menu.moveToXY(newMenuPosition.x, newMenuPosition.y);
                    }
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);

        }


        scrollComponentIntoView(component:LiveEdit.component.Component):void {
            var dimensions:ElementDimensions = component.getElementDimensions();
            if (dimensions.top <= window.pageYOffset) {
                $('html, body').animate({scrollTop: dimensions.top - 10}, 200);
            }
        }
    }
}