module APP_context {

    export class SpaceContext  {

        private static context:SpaceContext;

        private selectedSpaces:APP.model.SpaceModel[];

        static init():SpaceContext{
            return context = new SpaceContext();
        }

        static get():SpaceContext{
            return context;
        }

        constructor(){
            APP.event.GridSelectionChangeEvent.on((event) => {
                this.selectedSpaces = event.getModel();
            });
        }

        getSelectedSpaces():APP.model.SpaceModel[] {
            return this.selectedSpaces;
        }
    }
}
