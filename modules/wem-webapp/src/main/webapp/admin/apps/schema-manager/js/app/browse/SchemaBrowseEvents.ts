module app_browse {

    export class BaseSchemaModelEvent extends api_event.Event {
        private model:api_schema.Schema[];

        constructor(name:string, model:api_schema.Schema[]) {
            this.model = model;
            super(name);
        }

        getSchemas():api_schema.Schema[] {
            return this.model;
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

        constructor(model:api_schema.Schema[]) {
            super('editSchema', model);
        }

        static on(handler:(event:EditSchemaEvent) => void) {
            api_event.onEvent('editSchema', handler);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_schema.Schema[]) {
            super('openSchema', model);
        }

        static on(handler:(event:OpenSchemaEvent) => void) {
            api_event.onEvent('openSchema', handler);
        }
    }

    export class DeleteSchemaPromptEvent extends BaseSchemaModelEvent {

        constructor(model:api_schema.Schema[]) {
            super('deleteSchema', model);
        }

        static on(handler:(event:DeleteSchemaPromptEvent) => void) {
            api_event.onEvent('deleteSchema', handler);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_schema.Schema[]) {
            super('reindexSchema', model);
        }

        static on(handler:(event:ReindexSchemaEvent) => void) {
            api_event.onEvent('reindexSchema', handler);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api_schema.Schema[]) {
            super('exportSchema', model);
        }

        static on(handler:(event:ExportSchemaEvent) => void) {
            api_event.onEvent('exportSchema', handler);
        }
    }

    export class CloseSchemaEvent extends api_event.Event {

        private panel:api_ui.Panel;

        private checkCanRemovePanel:boolean;

        constructor(panel:api_ui.Panel, checkCanRemovePanel:boolean = true) {
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
}