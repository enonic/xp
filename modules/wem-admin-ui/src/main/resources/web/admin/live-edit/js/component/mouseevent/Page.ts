module LiveEdit.component.mouseevent {

    import PageItemType = api.liveedit.PageItemType;

    export class Page extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = PageItemType.get().getConfig().getCssSelector();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}
