module app.browse {

    export class ShowPreviewEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('showPreview', model);
        }

        static on(handler:(event:ShowPreviewEvent) => void) {
            api.event.onEvent('ShowPreview', handler);
        }

    }
}
