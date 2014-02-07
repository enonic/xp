module app.browse.event {
    export class EditTemplateEvent extends api.event.Event {

        private templates: api.content.site.template.SiteTemplateSummary[];

        constructor(templates: api.content.site.template.SiteTemplateSummary[]) {
            super('editTemplate');
            this.templates = templates;
        }

        static on(handler: (event: EditTemplateEvent) => void) {
            api.event.onEvent('editTemplate', handler);
        }

        public getTemplates(): api.content.site.template.SiteTemplateSummary[] {
            return this.templates;
        }
    }
}