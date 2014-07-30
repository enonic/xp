module app.browse {

    export class ContentDeletePromptEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:ContentDeletePromptEvent) => void) {
            api.event.onEvent('deleteContent', handler);
        }
    }
}
