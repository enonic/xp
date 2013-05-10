module LiveEdit.model {
    var $ = $liveedit;

    export class Layout extends LiveEdit.model.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=layout]';

            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Layout model instantiated. Using jQuery ' + $().jquery);
        }
    }
}