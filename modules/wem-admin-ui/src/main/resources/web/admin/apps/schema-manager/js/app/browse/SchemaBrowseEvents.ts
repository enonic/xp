module app.browse {

    import Event = api.event.Event;
    import Schema = api.schema.Schema;

    export class BaseSchemaModelEvent extends Event {
        private model: Schema[];

        constructor(model: Schema[]) {
            this.model = model;
            super();
        }

        getSchemas(): Schema[] {
            return this.model;
        }
    }

    export class ShowNewSchemaDialogEvent extends Event {

        static on(handler: (event: ShowNewSchemaDialogEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ShowNewSchemaDialogEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class EditSchemaEvent extends BaseSchemaModelEvent {

        static on(handler: (event: EditSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: EditSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class OpenSchemaEvent extends BaseSchemaModelEvent {

        static on(handler: (event: OpenSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: OpenSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class DeleteSchemaPromptEvent extends BaseSchemaModelEvent {

        static on(handler: (event: DeleteSchemaPromptEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: DeleteSchemaPromptEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class ReindexSchemaEvent extends BaseSchemaModelEvent {

        static on(handler: (event: ReindexSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ReindexSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class ExportSchemaEvent extends BaseSchemaModelEvent {

        static on(handler: (event: ExportSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ExportSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

    export class CloseSchemaEvent extends Event {

        private panel: api.ui.panel.Panel;

        private checkCanRemovePanel: boolean;

        constructor(panel: api.ui.panel.Panel, checkCanRemovePanel: boolean = true) {
            super();
            this.panel = panel;
            this.checkCanRemovePanel = checkCanRemovePanel;
        }

        getPanel(): api.ui.panel.Panel {
            return this.panel;
        }

        isCheckCanRemovePanel() {
            return this.checkCanRemovePanel;
        }

        static on(handler: (event: CloseSchemaEvent) => void, contextWindow: Window = window) {
            Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: CloseSchemaEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}