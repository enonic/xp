module api.application {

    export class ApplicationKey implements api.Equitable {

        static SYSTEM: ApplicationKey = ApplicationKey.fromString('system');
        static BASE: ApplicationKey = ApplicationKey.fromString('base');
        static PORTAL: ApplicationKey = ApplicationKey.fromString('portal');
        static MEDIA: ApplicationKey = ApplicationKey.fromString('media');

        static SYSTEM_RESERVED_APPLICATION_KEYS = [
            ApplicationKey.SYSTEM,
            ApplicationKey.BASE,
            ApplicationKey.PORTAL,
            ApplicationKey.MEDIA];

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

        isSystemReserved(): boolean {
            for (var key in ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS) {
                if (ApplicationKey.SYSTEM_RESERVED_APPLICATION_KEYS[key].equals(this)) {
                    return true;
                }
            }
            return false;
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