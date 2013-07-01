module app {

    export class SchemaContext {

        private static context:SchemaContext;

        private selectedSchema:api_model.SchemaModel;

        static init():SchemaContext {
            return context = new SchemaContext();
        }

        static get():SchemaContext {
            return context;
        }

        constructor() {
            app_browse.GridSelectionChangeEvent.on((event) => {
                this.selectedSchema = event.getModel();
            });
        }

        getSelectedSchema():api_model.SchemaModel {
            return this.selectedSchema;
        }
    }

}