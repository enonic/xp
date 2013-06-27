module app_event {

    export class ShowDetailsEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api_event.onEvent('ShowDetails', handler);
        }

    }

}