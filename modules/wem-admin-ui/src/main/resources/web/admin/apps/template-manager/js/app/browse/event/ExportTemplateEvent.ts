module app.browse.event {

    import Event2 = api.event.Event2;

    export class ExportTemplateEvent extends Event2 {

        private siteTemplate: api.content.TemplateSummary;

        constructor(siteTemplate: api.content.TemplateSummary) {
            this.siteTemplate = siteTemplate;
            super('exportTemplate');
        }

        getTemplate(): api.content.TemplateSummary {
            return this.siteTemplate;
        }

        static on(handler: (event: ExportTemplateEvent) => void, contextWindow: Window = window) {
            Event2.bind("exportTemplate", handler, contextWindow);
        }

        static un(handler: (event: ExportTemplateEvent) => void, contextWindow: Window = window) {
            Event2.unbind("exportTemplate", handler, contextWindow);
        }
    }
}