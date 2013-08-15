module app_browse {

    export class BaseSchemaModelEvent extends api_event.Event {
        private model:api_model.SchemaExtModel[];

        constructor(name:string, model:api_model.SchemaExtModel[]) {
            this.model = model;
            super(name);
        }

        getModels():api_model.SchemaExtModel[] {
            return this.model;
        }
    }

    export class GridSelectionChangeEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('gridChange', model);
        }

        static on(handler:(event:GridSelectionChangeEvent) => void) {
            api_event.onEvent('gridChange', handler);
        }
    }

    export class ShowNewSchemaDialogEvent extends api_event.Event {

        constructor() {
            super('showNewSchemaDialog');
        }

        static on(handler:(event:ShowNewSchemaDialogEvent) => void) {
            api_event.onEvent('showNewSchemaDialog', handler);
        }
    }

    export class EditSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('editSchema', model);
        }

        static on(handler:(event:EditSchemaEvent) => void) {
            api_event.onEvent('editSchema', handler);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('openSchema', model);
        }

        static on(handler:(event:OpenSchemaEvent) => void) {
            api_event.onEvent('openSchema', handler);
        }
    }

    export class DeleteSchemaPromptEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('deleteSchema', model);
        }

        static on(handler:(event:DeleteSchemaPromptEvent) => void) {
            api_event.onEvent('deleteSchema', handler);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('reindexSchema', model);
        }

        static on(handler:(event:ReindexSchemaEvent) => void) {
            api_event.onEvent('reindexSchema', handler);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_model.SchemaExtModel[]) {
            super('exportSchema', model);
        }

        static on(handler:(event:ExportSchemaEvent) => void) {
            api_event.onEvent('exportSchema', handler);
        }
    }

    export class CloseSchemaEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:bool;

        constructor(panel:api_ui.Panel, checkCanRemovePanel?:bool = true) {
            super('closeSchema');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api_ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseSchemaEvent) => void) {
            api_event.onEvent('closeSchema', handler);
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