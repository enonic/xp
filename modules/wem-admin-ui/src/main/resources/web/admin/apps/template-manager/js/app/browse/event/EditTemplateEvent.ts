module app.browse.event {

    import Event = api.event.Event;

    export class EditTemplateEvent extends Event {

        private templates: api.content.TemplateSummary[];

        constructor(templates: api.content.TemplateSummary[]) {
            super('editTemplate');
            this.templates = templates;
        }

        public getTemplates(): api.content.TemplateSummary[] {
            return this.templates;
        }

        static on(handler: (event: EditTemplateEvent) => void, contextWindow: Window = window) {
            Event.bind("editTemplate", handler, contextWindow);
        }

        static un(handler: (event: EditTemplateEvent) => void, contextWindow: Window = window) {
            Event.unbind("editTemplate", handler, contextWindow);
        }
    }
}