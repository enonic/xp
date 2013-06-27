module app_event {

    export class ShowPreviewEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api_event.onEvent('ShowPreview', handler);
        }

    }

}