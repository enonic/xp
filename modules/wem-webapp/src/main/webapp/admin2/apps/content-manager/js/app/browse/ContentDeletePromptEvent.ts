module app_browse {

    export class ContentDeletePromptEvent extends app_event.BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('deleteContent', model);
        }

        static on(handler:(event:ContentDeletePromptEvent) => void) {
            api_event.onEvent('deleteContent', handler);
        }
    }

}
