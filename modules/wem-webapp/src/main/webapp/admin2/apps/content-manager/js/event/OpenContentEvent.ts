module app_event {

    export class OpenContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('openContent', model);
        }

        static on(handler:(event:OpenContentEvent) => void) {
            api_event.onEvent('openContent', handler);
        }
    }

}
