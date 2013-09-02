module LiveEdit.component.listener {

    // Uses
    var $ = $liveEdit;

    export class Content extends LiveEdit.component.listener.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.CONTENT].cssSelector;

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}