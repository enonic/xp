module api {

    export class Client {

        private static instance: Client;

        private id: string;

        private propertyIdProvider: api.data2.PropertyIdProvider;

        static get(): Client {

            var w = api.dom.WindowDOM.get();
            var topWindow: any = w.getTopParent().asWindow();

            if (!topWindow.api.Client.instance) {
                topWindow.api.Client.instance = new Client();
            }
            return topWindow.api.Client.instance;
        }

        constructor() {
            this.id = Date.now().toString();
            this.propertyIdProvider = new api.data2.DefaultPropertyIdProvider(this.id);
        }

        getId(): string {
            return this.id;
        }

        getPropertyIdProvider(): api.data2.PropertyIdProvider {
            return this.propertyIdProvider;
        }
    }
}