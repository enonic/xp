module app_event {

    export class MoveContentEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('moveContent', model);
        }

        static on(handler:(event:MoveContentEvent) => void) {
            api_event.onEvent('moveContent', handler);
        }

    }

}