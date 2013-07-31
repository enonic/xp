module app {

    export class SpaceContext  {

        private static context:SpaceContext;

        private selectedSpaces:api_model.SpaceExtModel[];

        static init():SpaceContext{
            return context = new SpaceContext();
        }

        static get():SpaceContext{
            return context;
        }

        constructor(){
            app_browse.GridSelectionChangeEvent.on((event) => {
                this.selectedSpaces = event.getModels();
            });
        }

        getSelectedSpaces():api_model.SpaceExtModel[] {
            return this.selectedSpaces;
        }
    }
}
