module LiveEdit.ui.contextmenu.menuitem {

    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;

    // Uses
    var $ = $liveEdit;

    export class EmptyRegionMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Empty',
                name: 'clearRegion',
                handler: (event:Event) => {
                    this.onEmptyRegion();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onEmptyRegion() {
            var region:JQuery = this.menu.selectedComponent.getElement();

            LiveEdit.component.Selection.deselect();

            $('[data-live-edit-type]', region).remove();

            new PageComponentRemoveEvent(this.menu.selectedComponent.getComponentPath()).fire();
        }
    }
}