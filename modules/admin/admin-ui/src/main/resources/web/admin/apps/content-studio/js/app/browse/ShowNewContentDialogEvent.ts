module app.browse {

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent: api.content.ContentSummaryAndCompareStatus;

        constructor(parentContent: api.content.ContentSummaryAndCompareStatus) {
            super([parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent(): api.content.ContentSummaryAndCompareStatus {
            return this.parentContent;
        }

        static on(handler: (event: ShowNewContentDialogEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowNewContentDialogEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}
