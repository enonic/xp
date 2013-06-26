module app_event {

    export class DuplicateContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api_event.onEvent('duplicateContent', handler);
        }

    }

}