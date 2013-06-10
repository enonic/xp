module app_event {

    export class BaseSpaceModelEvent extends api_event.Event {
        private model:app_model.SpaceModel[];

        constructor(name:string, model:app_model.SpaceModel[]) {
            this.model = model;
            super(name);
        }

        getSpaceModels():app_model.SpaceModel[] {
            return this.model;
        }
    }

}
