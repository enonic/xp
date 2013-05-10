module LiveEdit.model {
    var $ = $liveedit;

    export class Part extends LiveEdit.model.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=part]';

            this.renderEmptyPlaceholders();
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();

            console.log('Part model instantiated. Using jQuery ' + $().jquery);
        }

        private renderEmptyPlaceholders() {
            var parts = this.getAll(),
                part:JQuery;
            parts.each((i) => {
                part = $(parts[i]);
                if (this.isPartEmpty(part)) {
                    this.appendEmptyPlaceholder(part);
                }
            });
        }

        private appendEmptyPlaceholder(part:JQuery) {
            var $placeholder = $('<div/>', {
                'class': 'live-edit-empty-part-placeholder',
                'html': 'Empty Part'
            });
            part.append($placeholder);
        }

        private isPartEmpty(part:JQuery) {
            return $(part).children().length === 0;
        }

    }
}
