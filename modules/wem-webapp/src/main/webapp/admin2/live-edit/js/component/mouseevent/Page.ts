module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Page extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.PAGE].cssSelector;

            // Only attach click event.
            // Page should not have hover/out event
            this.attachClickEvent();
        }
    }
}
