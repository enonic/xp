module LiveEdit.component.eventdispatcher {
    var $ = $liveEdit;

    export class Part extends LiveEdit.component.eventdispatcher.Base {
        constructor() {
            super();

            this.componentCssSelector = '[data-live-edit-type=part]';

            this.renderEmptyPlaceholders();
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }

        private renderEmptyPlaceholders():void {
            var parts = this.getAll(),
                part:JQuery;
            parts.each((i) => {
                part = $(parts[i]);
                if (this.isPartEmpty(part)) {
                    this.appendEmptyPlaceholder(part);
                }
            });
        }

        private appendEmptyPlaceholder(part:JQuery):void {
            var placeholder:JQuery = $('<div/>', {
                'class': 'live-edit-empty-part-placeholder',
                'html': 'Empty Part'
            });
            part.append(placeholder);
        }

        private isPartEmpty(part:JQuery):Boolean {
            return $(part).children().length === 0;
        }

    }
}
