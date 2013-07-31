module app {

    export class SchemaContext {

        private static context:SchemaContext;

        private selectedSchemes:api_model.SchemaExtModel[];

        static init():SchemaContext {
            return context = new SchemaContext();
        }

        static get():SchemaContext {
            return context;
        }

        constructor() {
            app_browse.GridSelectionChangeEvent.on((event) => {
                this.selectedSchemes = event.getModels();
            });
        }

        getSelectedSchemes():api_model.SchemaExtModel[] {
            return this.selectedSchemes;
        }
    }

}