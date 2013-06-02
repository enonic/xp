module APP.event {

    export class SpaceModelEvent extends API_event.Event {
        private model:APP.model.SpaceModel[];

        constructor(name:string, model:APP.model.SpaceModel[]) {
            this.model = model;
            super(name);
        }

        getModel():APP.model.SpaceModel[] {
            return this.model;
        }
    }

}
