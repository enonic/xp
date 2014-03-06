module app.browse.event {
    export class ExportTemplateEvent extends BaseTemplateModelEvent {

        constructor(siteTemplate: app.browse.TemplateSummary) {
            super('exportTemplate', [siteTemplate]);
        }

        static on(handler:(event:ExportTemplateEvent) => void) {
            api.event.onEvent('exportTemplate', handler);
        }

        getTemplate(): app.browse.TemplateSummary {
            return this.getTemplates()[0];
        }
    }
}