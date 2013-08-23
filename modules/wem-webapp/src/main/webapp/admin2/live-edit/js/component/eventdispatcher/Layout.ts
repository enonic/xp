module LiveEdit.component.eventdispatcher {
    var $ = $liveEdit;

    export class Layout extends LiveEdit.component.eventdispatcher.Base {
        constructor() {
            super();

            this.componentCssSelector = '[data-live-edit-type=layout]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}