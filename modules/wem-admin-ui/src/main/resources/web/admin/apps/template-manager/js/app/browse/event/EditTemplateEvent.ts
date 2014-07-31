module app.browse.event {

    import Event2 = api.event.Event2;

    export class EditTemplateEvent extends Event2 {

        private templates: api.content.TemplateSummary[];

        constructor(templates: api.content.TemplateSummary[]) {
            super('editTemplate');
            this.templates = templates;
        }

        public getTemplates(): api.content.TemplateSummary[] {
            return this.templates;
        }

        static on(handler: (event: EditTemplateEvent) => void, contextWindow: Window = window) {
            Event2.bind("editTemplate", handler, contextWindow);
        }

        static un(handler: (event: EditTemplateEvent) => void, contextWindow: Window = window) {
            Event2.unbind("editTemplate", handler, contextWindow);
        }
    }
}