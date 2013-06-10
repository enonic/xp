module app_event {

    export class BaseSpaceModelEvent extends api_event.Event {
        private model:api_model.SpaceModel[];

        constructor(name:string, model:api_model.SpaceModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.SpaceModel[] {
            return this.model;
        }
    }

}
