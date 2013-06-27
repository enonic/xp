module app_event {

    export class EditContentEvent extends BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('editContent', model);
        }

        static on(handler:(event:EditContentEvent) => void) {
            api_event.onEvent('editContent', handler);
        }
    }

}
