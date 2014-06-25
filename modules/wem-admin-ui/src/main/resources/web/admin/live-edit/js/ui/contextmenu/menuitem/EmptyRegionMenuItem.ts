module LiveEdit.ui.contextmenu.menuitem {

    import RegionView = api.liveedit.RegionView;

    export class EmptyRegionMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        constructor(menu) {
            super({
                text: 'Empty',
                name: 'clearRegion',
                handler: (event: Event) => {
                    this.onEmptyRegion();
                    event.stopPropagation();
                }
            }, menu);

            this.menu = menu;
        }

        private onEmptyRegion() {

            var selectedItem = this.menu.selectedComponent;
            if (api.ObjectHelper.iFrameSafeInstanceOf(selectedItem, RegionView)) {
                var selectedRegionView = <RegionView>selectedItem;

                selectedRegionView.deselect();
                selectedRegionView.empty();
            }
            else {
                throw new Error("Expected region to empty, got [" + api.util.getClassName(selectedItem) + "]");
            }
        }
    }
}