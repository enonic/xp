module app.browse.event {

    export class BaseTemplateModelEvent extends api.event.Event {
        private model: app.browse.TemplateSummary[];

        constructor(name: string, model: app.browse.TemplateSummary[]) {
            this.model = model;
            super(name);
        }

        getTemplates(): app.browse.TemplateSummary[] {
            return this.model;
        }
    }
}