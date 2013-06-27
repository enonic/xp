module app_browse {

    export class DuplicateContentEvent extends app_event.BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('duplicateContent', model);
        }

        static on(handler:(event:DuplicateContentEvent) => void) {
            api_event.onEvent('duplicateContent', handler);
        }

    }

}