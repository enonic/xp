module LiveEdit.component.mouseevent {

    import ContentItemType = api.liveedit.ContentItemType;

    export class Content extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = ContentItemType.get().getConfig().getCssSelector();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}