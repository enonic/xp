module app.browse.event {
    export class EditTemplateEvent extends api.event.Event {

        private templates: api.content.TemplateSummary[];

        constructor(templates: api.content.TemplateSummary[]) {
            super('editTemplate');
            this.templates = templates;
        }

        static on(handler: (event: EditTemplateEvent) => void) {
            api.event.onEvent('editTemplate', handler);
        }

        public getTemplates(): api.content.TemplateSummary[] {
            return this.templates;
        }
    }
}