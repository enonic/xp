module app_browse {

    export class GridSelectionChangeEvent extends app_event.BaseContentModelEvent {
        constructor(model:api_model.ContentModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }
}
