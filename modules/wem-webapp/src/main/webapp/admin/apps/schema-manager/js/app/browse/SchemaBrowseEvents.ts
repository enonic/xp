module app.browse {

    export class BaseSchemaModelEvent extends api.event.Event {
        private model:api.schema.Schema[];

        constructor(name:string, model:api.schema.Schema[]) {
            this.model = model;
            super(name);
        }

        getSchemas():api.schema.Schema[] {
            return this.model;
        }
    }

    export class ShowNewSchemaDialogEvent extends api.event.Event {

        constructor() {
            super('showNewSchemaDialog');
        }

        static on(handler:(event:ShowNewSchemaDialogEvent) => void) {
            api.event.onEvent('showNewSchemaDialog', handler);
        }
    }

    export class EditSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api.schema.Schema[]) {
            super('editSchema', model);
        }

        static on(handler:(event:EditSchemaEvent) => void) {
            api.event.onEvent('editSchema', handler);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api.schema.Schema[]) {
            super('openSchema', model);
        }

        static on(handler:(event:OpenSchemaEvent) => void) {
            api.event.onEvent('openSchema', handler);
        }
    }

    export class DeleteSchemaPromptEvent extends BaseSchemaModelEvent {

        constructor(model:api.schema.Schema[]) {
            super('deleteSchema', model);
        }

        static on(handler:(event:DeleteSchemaPromptEvent) => void) {
            api.event.onEvent('deleteSchema', handler);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api.schema.Schema[]) {
            super('reindexSchema', model);
        }

        static on(handler:(event:ReindexSchemaEvent) => void) {
            api.event.onEvent('reindexSchema', handler);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        constructor(model:api.schema.Schema[]) {
            super('exportSchema', model);
        }

        static on(handler:(event:ExportSchemaEvent) => void) {
            api.event.onEvent('exportSchema', handler);
        }
    }

    export class CloseSchemaEvent extends api.event.Event {

        private panel:api.ui.Panel;

        private checkCanRemovePanel:boolean;

        constructor(panel:api.ui.Panel, checkCanRemovePanel:boolean = true) {
            super('closeSchema');
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel():api.ui.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler:(event:CloseSchemaEvent) => void) {
            api.event.onEvent('closeSchema', handler);
        }
    }
}