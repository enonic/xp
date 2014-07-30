module app.browse.event {

    export class BaseTemplateModelEvent extends api.event.Event {
        private model: api.content.TemplateSummary[];

        constructor(name: string, model: api.content.TemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getTemplates(): api.content.TemplateSummary[] {
            return this.model;
        }
    }
}