module LiveEdit.component.observer {
    var $ = $liveEdit;

    export class Image extends LiveEdit.component.observer.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=image]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Image observer instantiated. Using jQuery ' + $().jquery);
        }
    }
}