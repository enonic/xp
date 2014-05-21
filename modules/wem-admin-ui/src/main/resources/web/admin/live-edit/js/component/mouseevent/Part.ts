module LiveEdit.component.mouseevent {

    import PartItemType = api.liveedit.part.PartItemType;

    export class Part extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = PartItemType.get().getConfig().getCssSelector();

            this.renderEmptyPlaceholders();
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
        }

        private renderEmptyPlaceholders(): void {
            var parts = this.getAll(),
                part: JQuery;
            parts.each((i) => {
                part = $(parts[i]);
                if (this.isPartEmpty(part)) {
                    this.appendEmptyPlaceholder(part);
                }
            });
        }

        private appendEmptyPlaceholder(part: JQuery): void {
            var placeholder: JQuery = $('<div/>', {
                'class': 'live-edit-empty-part-placeholder',
                'html': 'Empty Part'
            });
            part.append(placeholder);
        }

        private isPartEmpty(part: JQuery): Boolean {
            return $(part).children().length === 0;
        }

    }
}
