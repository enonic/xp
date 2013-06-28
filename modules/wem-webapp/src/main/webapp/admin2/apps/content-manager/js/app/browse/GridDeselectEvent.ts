module app_browse {

    export class GridDeselectEvent extends app_event.BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
        }
    }
}
