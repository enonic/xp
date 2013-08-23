module LiveEdit.component.eventdispatcher {
    var $ = $liveEdit;

    export class Image extends LiveEdit.component.eventdispatcher.Base {
        constructor() {
            super();

            this.componentCssSelector = '[data-live-edit-type=image]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }
    }
}