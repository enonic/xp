module LiveEdit.component.listener {

    // Uses
    var $ = $liveEdit;

    export class Image extends LiveEdit.component.listener.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.IMAGE].cssSelector;

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}