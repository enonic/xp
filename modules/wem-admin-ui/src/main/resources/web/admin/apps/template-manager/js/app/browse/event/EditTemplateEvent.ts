module app.browse.event {
    export class EditTemplateEvent extends api.event.Event {

        private templates: app.browse.TemplateSummary[];

        constructor(templates: app.browse.TemplateSummary[]) {
            super('editTemplate');
            this.templates = templates;
        }

        static on(handler: (event: EditTemplateEvent) => void) {
            api.event.onEvent('editTemplate', handler);
        }

        public getTemplates(): app.browse.TemplateSummary[] {
            return this.templates;
        }
    }
}