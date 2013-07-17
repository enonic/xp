module app {

    export class SchemaContext {

        private static context:SchemaContext;

        private selectedSchemas:api_model.SchemaModel[];

        static init():SchemaContext {
            return context = new SchemaContext();
        }

        static get():SchemaContext {
            return context;
        }

        constructor() {
            app_browse.GridSelectionChangeEvent.on((event) => {
                this.selectedSchemas = event.getModels();
            });
        }

        getSelectedSchema():api_model.SchemaModel[] {
            return this.selectedSchemas;
        }
    }

}