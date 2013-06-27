module app_browse {

    export class ShowDetailsEvent extends app_event.BaseContentModelEvent {

        constructor(model:api_model.ContentModel[]) {
            super('showDetails', model);
        }

        static on(handler:(event:ShowDetailsEvent) => void) {
            api_event.onEvent('ShowDetails', handler);
        }

    }

}