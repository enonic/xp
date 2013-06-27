module app_browse {

    export class MoveContentEvent extends app_event.BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('moveContent', model);
        }

        static on(handler:(event:MoveContentEvent) => void) {
            api_event.onEvent('moveContent', handler);
        }

    }

}