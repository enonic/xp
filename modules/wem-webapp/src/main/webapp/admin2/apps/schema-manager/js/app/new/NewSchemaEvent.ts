module app_new {

    export class NewSchemaEvent extends api_event.Event {

        private schemaType:string;

        constructor(schemaType?:string) {
            super('newSchema');

            this.schemaType = schemaType;
        }

        static on(handler:(event:NewSchemaEvent) => void) {
            api_event.onEvent('newSchema', handler);
        }

        getSchemaType():string {
            return this.schemaType;
        }
    }

}