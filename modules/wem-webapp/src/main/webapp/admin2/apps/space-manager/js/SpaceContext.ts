module app {

    export class SpaceContext  {

        private static context:SpaceContext;

        private selectedSpaces:app_model.SpaceModel[];

        static init():SpaceContext{
            return context = new SpaceContext();
        }

        static get():SpaceContext{
            return context;
        }

        constructor(){
            app_event.GridSelectionChangeEvent.on((event) => {
                this.selectedSpaces = event.getModel();
            });
        }

        getSelectedSpaces():app_model.SpaceModel[] {
            return this.selectedSpaces;
        }
    }
}
