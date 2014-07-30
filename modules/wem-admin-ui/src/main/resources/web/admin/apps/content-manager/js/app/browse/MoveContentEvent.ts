module app.browse {

    export class MoveContentEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('moveContent', model);
        }

        static on(handler:(event:MoveContentEvent) => void) {
            api.event.onEvent('moveContent', handler);
        }

    }

}
