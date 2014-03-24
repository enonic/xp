module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Page extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.PAGE].cssSelector;

            this.attachClickEvent();
        }
    }
}
