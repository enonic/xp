module LiveEdit.model {
    var $ = $liveedit;

    export class Page extends LiveEdit.model.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=page]';
            this.attachClickEvent();

            console.log('Page model instantiated. Using jQuery ' + $().jquery);
        }
    }
}
