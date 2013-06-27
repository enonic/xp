module app_browse {

    export class DeleteContentEvent extends app_event.BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:DeleteContentEvent) => void) {
            api_event.onEvent('deleteContent', handler);
        }
    }

}
