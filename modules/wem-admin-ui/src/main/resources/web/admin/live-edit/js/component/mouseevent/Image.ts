module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Image extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.IMAGE].cssSelector;

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}