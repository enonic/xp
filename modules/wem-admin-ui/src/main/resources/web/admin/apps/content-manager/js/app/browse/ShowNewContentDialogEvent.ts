module app.browse {

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent:api.content.ContentSummary;

        constructor(parentContent:api.content.ContentSummary) {
            super([parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent():api.content.ContentSummary {
            return this.parentContent;
        }

        static on(handler: (event: ShowNewContentDialogEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ShowNewContentDialogEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}
