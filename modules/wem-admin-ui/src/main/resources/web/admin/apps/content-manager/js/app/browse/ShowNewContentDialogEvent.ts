module app.browse {

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent: api.content.ContentSummary;

        constructor(parentContent: api.content.ContentSummary) {
            super([parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent(): api.content.ContentSummary {
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
