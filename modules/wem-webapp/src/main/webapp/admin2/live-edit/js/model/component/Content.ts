module LiveEdit.model {
    var $ = $liveedit;

    export class Content extends LiveEdit.model.Base {
        constructor() {
            super();
            this.cssSelector = '[data-live-edit-type=content]';
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Content model instantiated. Using jQuery ' + $().jquery);
        }
    }
}