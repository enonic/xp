module app_browse {

    export class BaseSchemaModelEvent extends api_event.Event {
        private model:api_model.SchemaModel[];

        constructor(name:string, model:api_model.SchemaModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.SchemaModel[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class GridDeselectEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('removeFromGrid', model);
        }

        static on(handler:(event:GridDeselectEvent) => void) {
            api_event.onEvent('removeFromGrid', handler);
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

        constructor(model:api_model.SchemaModel[]) {
            super('editSchema', model);
        }

        static on(handler:(event:EditSchemaEvent) => void) {
            api_event.onEvent('editSchema', handler);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('openSchema', model);
        }

        static on(handler:(event:OpenSchemaEvent) => void) {
            api_event.onEvent('openSchema', handler);
        }
    }

    export class DeleteSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('deleteSchema', model);
        }

        static on(handler:(event:DeleteSchemaEvent) => void) {
            api_event.onEvent('deleteSchema', handler);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('reindexSchema', model);
        }

        static on(handler:(event:ReindexSchemaEvent) => void) {
            api_event.onEvent('reindexSchema', handler);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaModel[]) {
            super('exportSchema', model);
        }

        static on(handler:(event:ExportSchemaEvent) => void) {
            api_event.onEvent('exportSchema', handler);
        }
    }

    export class ShowContextMenuEvent extends api_event.Event {

        private x:number;
        private y:number;

        constructor(x:number, y:number) {
            this.x = x;
            this.y = y;
            super('showContextMenu');
        }

        getX() {
            return this.x;
        }

        getY() {
            return this.y;
        }

        static on(handler:(event:ShowContextMenuEvent) => void) {
            api_event.onEvent('showContextMenu', handler);
        }
    }

}