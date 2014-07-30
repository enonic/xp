module app.browse {

    export class ViewContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('openContent', model);
        }

        static on(handler:(event:ViewContentEvent) => void) {
            api.event.onEvent('openContent', handler);
        }
    }
}
