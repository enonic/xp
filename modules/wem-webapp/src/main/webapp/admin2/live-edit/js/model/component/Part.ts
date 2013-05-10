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


        appendEmptyPlaceholder($part) {
            var $placeholder = $('<div/>', {
                'class': 'live-edit-empty-part-placeholder',
                'html': 'Empty Part'
            });
            $part.append($placeholder);
        }


        isPartEmpty($part) {
            return $($part).children().length === 0;
        }


        renderEmptyPlaceholders() {
            var t = this;
            this.getAll().each(function (index) {
                var $part = $(this);
                var partIsEmpty = t.isPartEmpty($part);
                if (partIsEmpty) {
                    t.appendEmptyPlaceholder($part);
                }
            });
        }
    }
}
