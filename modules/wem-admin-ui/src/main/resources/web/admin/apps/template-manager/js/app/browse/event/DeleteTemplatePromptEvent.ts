module app.browse.event {

    import Event = api.event.Event;

    export class DeleteTemplatePromptEvent extends Event {

        private template: api.content.TemplateSummary;

        constructor(template: api.content.TemplateSummary) {
            this.template = template;
            super('deleteSiteTemplatePrompt');
        }

        getTemplate(): api.content.TemplateSummary {
            return this.template;
        }

        static on(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event.bind("deleteSiteTemplatePrompt", handler, contextWindow);
        }

        static un(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event.unbind("deleteSiteTemplatePrompt", handler, contextWindow);
        }
    }

}