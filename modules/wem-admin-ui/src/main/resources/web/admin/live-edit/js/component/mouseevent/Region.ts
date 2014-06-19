module LiveEdit.component.mouseevent {

    import RegionItemType = api.liveedit.RegionItemType;

    export class Region extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = RegionItemType.get().getConfig().getCssSelector();

            this.attachClickEvent();
        }
    }
}
