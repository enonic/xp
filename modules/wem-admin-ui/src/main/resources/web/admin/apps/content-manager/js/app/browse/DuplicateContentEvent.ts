module app.browse {

    export class DuplicateContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api.event.onEvent('duplicateContent', handler);
        }

    }

}
