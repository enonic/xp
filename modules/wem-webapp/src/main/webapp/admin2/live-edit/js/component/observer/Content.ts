module LiveEdit.component.observer {
    var $ = $liveEdit;

    export class Content extends LiveEdit.component.observer.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=content]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Content observer instantiated. Using jQuery ' + $().jquery);
        }
    }
}