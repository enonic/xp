module app.browse {

    export class ShowNewContentDialogEvent extends BaseContentModelEvent {

        private parentContent:api.content.ContentSummary;

        constructor(parentContent:api.content.ContentSummary) {
            super('showNewContentDialog', [parentContent]);
            this.parentContent = parentContent;
        }

        getParentContent():api.content.ContentSummary {
            return this.parentContent;
        }

        static on(handler:(event:ShowNewContentDialogEvent) => void) {
            api.event.onEvent('showNewContentDialog', handler);
        }
    }
}
