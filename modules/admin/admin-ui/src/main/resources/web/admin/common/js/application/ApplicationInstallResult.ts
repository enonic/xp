module api.application {

    export class ApplicationInstallResult {

        private application: Application;

        private failure: string;

        setFailure(value: string) {
            this.failure = value;
        }

        setApplication(application: Application) {
            this.application = application;
        }

        public getApplication(): api.application.Application {
            return this.application;
        }

        public getFailure(): string {
            return this.failure;
        }

        static fromJson(json: json.ApplicationInstallResultJson) : ApplicationInstallResult {
            let result = new ApplicationInstallResult();
            result.application = json.applicationJson ? Application.fromJson(json.applicationJson) : null;
            result.failure = json.failure;
            return result;
        }
    }
}