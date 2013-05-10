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

        init() {
            var me = this;
            var $button = me.createButton({
                id: 'live-edit-button-parent',
                text: 'Select Parent',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();


                    var $parent = me.menu.selectedComponent.parents('[data-live-edit-type]');
                    if ($parent && $parent.length > 0) {
                        $parent = $($parent[0]);

                        $(window).trigger('component.onSelect', [$parent, {x: 0, y: 0}]);

                        me.scrollComponentIntoView($parent);

                        // Force position of the menu after component is selected.
                        // We could move this code to menu show.
                        // The position needs to be updated after menu is updated with info in order to get the right dimensions (width) of the menu.
                        var menuWidth = me.menu.getEl().outerWidth();
                        var componentBox = componentHelper.getBoxModel($parent),
                            newMenuPosition = {x: componentBox.left + (componentBox.width / 2) - (menuWidth / 2), y: componentBox.top + 10};

                        me.menu.moveToXY(newMenuPosition.x, newMenuPosition.y);
                    }
                }
            });

            me.appendTo(this.menu.getEl());
            me.menu.buttons.push(me);

        }


        scrollComponentIntoView($component) {
            var componentTopPosition = componentHelper.getPagePositionForComponent($component).top;
            if (componentTopPosition <= window.pageYOffset) {
                $('html, body').animate({scrollTop: componentTopPosition - 10}, 200);
            }
        }
    }
}