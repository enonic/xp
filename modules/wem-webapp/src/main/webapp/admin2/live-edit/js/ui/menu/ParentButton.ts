module LiveEdit.ui {
    var $ = $liveedit;
    var componentHelper = LiveEdit.ComponentHelper;

    export class ParentButton extends LiveEdit.ui.BaseButton {

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
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();


                    var $parent = this.menu.selectedComponent.parents('[data-live-edit-type]');
                    if ($parent && $parent.length > 0) {
                        $parent = $($parent[0]);

                        $(window).trigger('select.liveEdit.component', [$parent, {x: 0, y: 0}]);

                        this.scrollComponentIntoView($parent);

                        // Force position of the menu after component is selected.
                        // We could move this code to menu show.
                        // The position needs to be updated after menu is updated with info in order to get the right dimensions (width) of the menu.
                        var menuWidth = this.menu.getRootEl().outerWidth();
                        var componentBox = componentHelper.getBoxModel($parent),
                            newMenuPosition = {x: componentBox.left + (componentBox.width / 2) - (menuWidth / 2), y: componentBox.top + 10};

                        this.menu.moveToXY(newMenuPosition.x, newMenuPosition.y);
                    }
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);

        }


        scrollComponentIntoView($component):void {
            var componentTopPosition = componentHelper.getPagePositionForComponent($component).top;
            if (componentTopPosition <= window.pageYOffset) {
                $('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
            }
        }
    }
}