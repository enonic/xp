module app.browse {

    export class ShowDetailsEvent extends BaseContentModelEvent {

        constructor(model:api.content.ContentSummary[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api.event.onEvent('ShowDetails', handler);
        }

    }
}
