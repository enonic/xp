module LiveEdit.component.observer {
    var $ = $liveEdit;

    export class Page extends LiveEdit.component.observer.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=page]';

            this.attachClickEvent();

            console.log('Page observer instantiated. Using jQuery ' + $().jquery);
        }
    }
}
