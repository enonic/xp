module LiveEdit.component.listener {

    // Uses
    var $ = $liveEdit;

    export class Page extends LiveEdit.component.listener.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.PAGE].cssSelector;

            // Only attach click event.
            // Page should not have hover/out event
            this.attachClickEvent();
        }
    }
}
