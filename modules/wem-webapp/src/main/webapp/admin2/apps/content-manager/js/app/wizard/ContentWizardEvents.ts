module app_wizard {
    export class BaseContentModelEvent extends api_event.Event {

        private model:api_model.ContentExtModel[];

        constructor(name:string, model:api_model.ContentExtModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.ContentExtModel[] {
            return this.model;
        }
    }

    export class ShowContentFormEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentExtModel[]) {
            super('showContentForm', model);
        }

        static on(handler:(event:ShowContentFormEvent) => void) {
            api_event.onEvent('showContentForm', handler);
        }

    }

    export class ShowContentLiveEvent extends BaseContentModelEvent {

        constructor(model:api_model.ContentExtModel[]) {
            super('showContentLive', model);
        }

        static on(handler:(event:ShowContentLiveEvent) => void) {
            api_event.onEvent('showContentLive', handler);
        }

    }
}