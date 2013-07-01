module app_browse {

    export class BaseSchemaModelEvent extends api_event.Event {
        private model:api_model.SchemaModel;

        constructor(name:string, model:api_model.SchemaModel) {
            this.model = model;
            super(name);
        }

        getModel():api_model.SchemaModel {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class NewSchemaEvent extends api_event.Event {

        constructor() {
            super('newSchema');
        }

        static on(handler:(event:NewSchemaEvent) => void) {
            api_event.onEvent('newSchema', handler);
        }
    }

    export class EditSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('editSchema', model);
        }

        static on(handler:(event:EditSchemaEvent) => void) {
            api_event.onEvent('editSchema', handler);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('openSchema', model);
        }

        static on(handler:(event:OpenSchemaEvent) => void) {
            api_event.onEvent('openSchema', handler);
        }
    }

    export class DeleteSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('deleteSchema', model);
        }

        static on(handler:(event:DeleteSchemaEvent) => void) {
            api_event.onEvent('deleteSchema', handler);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('reindexSchema', model);
        }

        static on(handler:(event:ReindexSchemaEvent) => void) {
            api_event.onEvent('reindexSchema', handler);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel) {
            super('exportSchema', model);
        }

        static on(handler:(event:ExportSchemaEvent) => void) {
            api_event.onEvent('exportSchema', handler);
        }
    }
}