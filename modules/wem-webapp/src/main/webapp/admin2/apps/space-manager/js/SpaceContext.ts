module app {

    export class SpaceContext  {

        private static context:SpaceContext;

        private selectedSpaces:api_model.SpaceModel[];

        static init():SpaceContext{
            return context = new SpaceContext();
        }

        static get():SpaceContext{
            return context;
        }

        constructor(){
            app_event.GridSelectionChangeEvent.on((event) => {
                this.selectedSpaces = event.getModels();
            });
        }

        getSelectedSpaces():api_model.SpaceModel[] {
            return this.selectedSpaces;
        }
    }
}
