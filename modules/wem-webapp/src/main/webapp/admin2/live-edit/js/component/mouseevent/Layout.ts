module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Layout extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.LAYOUT].cssSelector;

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}