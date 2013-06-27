module app_event {

    export class DeleteContentEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:DeleteContentEvent) => void) {
            api_event.onEvent('deleteContent', handler);
        }
    }

}
