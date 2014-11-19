module api.content {

    export class ContentNamedEvent extends api.event.Event {

        private wizard: api.app.wizard.WizardPanel<Content>;
        private content: Content;

        constructor(wizard: api.app.wizard.WizardPanel<Content>, content: Content) {
            super();
            this.wizard = wizard;
            this.content = content;
        }

        public getWizard(): api.app.wizard.WizardPanel<Content> {
            return this.wizard;
        }

        public getContent(): Content {
            return this.content;
        }

        static on(handler: (event: ContentNamedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentNamedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }

}