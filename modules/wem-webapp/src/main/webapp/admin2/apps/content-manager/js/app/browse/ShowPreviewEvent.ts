module app_browse {

    export class ShowPreviewEvent extends app_event.BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api_event.onEvent('ShowPreview', handler);
        }

    }

}