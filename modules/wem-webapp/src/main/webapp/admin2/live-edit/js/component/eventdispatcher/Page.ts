module LiveEdit.component.eventdispatcher {
    var $ = $liveEdit;

    export class Page extends LiveEdit.component.eventdispatcher.Base {
        constructor() {
            super();

            this.componentCssSelector = '[data-live-edit-type=page]';

            this.attachClickEvent();
        }
    }
}
