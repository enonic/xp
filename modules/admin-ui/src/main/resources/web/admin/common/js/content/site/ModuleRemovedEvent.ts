module api.content.site {

    import ApplicationKey = api.module.ApplicationKey;

    export class ModuleRemovedEvent {

        private applicationKey: ApplicationKey;

        constructor(applicationKey: ApplicationKey) {
            this.applicationKey = applicationKey;
        }

        getApplicationKey(): ApplicationKey {
            return this.applicationKey;
        }
    }
}