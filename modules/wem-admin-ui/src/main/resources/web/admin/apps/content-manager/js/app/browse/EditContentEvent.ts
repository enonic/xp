module app.browse {

    export class EditContentEvent extends BaseContentModelEvent {
        constructor(model:api.content.ContentSummary[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api.event.onEvent('editContent', handler);
        }
    }
}
