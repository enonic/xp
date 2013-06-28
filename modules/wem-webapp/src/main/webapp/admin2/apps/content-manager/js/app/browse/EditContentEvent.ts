module app_browse {

    export class EditContentEvent extends app_event.BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api_event.onEvent('editContent', handler);
        }
    }

}
