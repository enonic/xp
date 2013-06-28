module app_event {

    export class BaseContentModelEvent extends api_event.Event {
        private model:api_model.ContentModel[];

        constructor(name:string, model:api_model.ContentModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.ContentModel[] {
            return this.model;
        }
    }

}
