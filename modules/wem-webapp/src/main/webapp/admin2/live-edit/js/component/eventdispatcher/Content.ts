module LiveEdit.component.eventdispatcher {
    var $ = $liveEdit;

    export class Content extends LiveEdit.component.eventdispatcher.Base {
        constructor() {
            super();

            this.componentCssSelector = '[data-live-edit-type=content]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}