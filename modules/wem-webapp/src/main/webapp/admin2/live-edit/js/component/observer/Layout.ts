module LiveEdit.component.observer {
    var $ = $liveEdit;

    export class Layout extends LiveEdit.component.observer.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=layout]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Layout observer instantiated. Using jQuery ' + $().jquery);
        }
    }
}