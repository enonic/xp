module LiveEdit.component.mouseevent {

    import LayoutItemType = api.liveedit.layout.LayoutItemType;

    export class Layout extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LayoutItemType.get().getConfig().getCssSelector();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}