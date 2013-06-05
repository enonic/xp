module app_event {

    export class SpaceModelEvent extends api_event.Event {
        private model:app_model.SpaceModel[];

        constructor(name:string, model:app_model.SpaceModel[]) {
            this.model = model;
            super(name);
        }

        getModel():app_model.SpaceModel[] {
            return this.model;
        }
    }

}
