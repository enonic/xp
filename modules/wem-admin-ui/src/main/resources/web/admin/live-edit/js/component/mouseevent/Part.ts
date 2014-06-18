module LiveEdit.component.mouseevent {

    import PartItemType = api.liveedit.part.PartItemType;

    export class Part extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = PartItemType.get().getConfig().getCssSelector();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}
