module app_browse {

    export class OpenContentEvent extends app_event.BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('openContent', model);
        }

        static on(handler:(event:OpenContentEvent) => void) {
            api_event.onEvent('openContent', handler);
        }
    }

}
