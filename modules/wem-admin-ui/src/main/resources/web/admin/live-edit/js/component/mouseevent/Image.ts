module LiveEdit.component.mouseevent {

    import ImageItemType = api.liveedit.image.ImageItemType;

    export class Image extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = ImageItemType.get().getConfig().getCssSelector();

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}