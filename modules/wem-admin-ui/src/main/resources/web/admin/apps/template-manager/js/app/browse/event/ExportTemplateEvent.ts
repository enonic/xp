module app.browse.event {
    export class ExportTemplateEvent extends BaseTemplateModelEvent {

        constructor(siteTemplate: api.content.TemplateSummary) {
            super('exportTemplate', [siteTemplate]);
        }

        static on(handler:(event:ExportTemplateEvent) => void) {
            api.event.onEvent('exportTemplate', handler);
        }

        getTemplate(): api.content.TemplateSummary {
            return this.getTemplates()[0];
        }
    }
}