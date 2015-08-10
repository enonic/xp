module api.application {

    export class ApplicationKey implements api.Equitable {

        static BASE: ApplicationKey = ApplicationKey.fromString('base');
        static PORTAL: ApplicationKey = ApplicationKey.fromString('portal');
        static MEDIA: ApplicationKey = ApplicationKey.fromString('media');

        private name: string;

        public static fromString(applicationName: string): ApplicationKey {
            return new ApplicationKey(applicationName);
        }

        constructor(applicationName: string) {
            this.name = applicationName;
        }

        getName(): string {
            return this.name;
        }

        toString(): string {
            return this.name;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, ApplicationKey)) {
                return false;
            }

            var other = <ApplicationKey>o;
            return api.ObjectHelper.stringEquals(this.name, other.name);
        }

        static toStringArray(keys: ApplicationKey[]): string[] {
            return keys.map((key: ApplicationKey) => key.toString());
        }

        static fromApplications(applications: Application[]): ApplicationKey[] {
            return applications.map<ApplicationKey>((mod: Application) => mod.getApplicationKey());
        }

    }
}