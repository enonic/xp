module app.home {

    export class Branding extends api.dom.DivEl {

        private installation: InstallationInfo;
        private version: VersionInfo;

        constructor(installation?: string, version?: string) {
            super('branding');

            installation = installation || '';
            version = version || '';

            this.installation = new InstallationInfo(installation);
            this.version = new VersionInfo(version);
            this.appendChild(this.installation);
            this.appendChild(this.version);

            new api.system.StatusRequest().send().done((response: api.rest.JsonResponse<api.system.StatusJson>) => {
                var result = response.getResult();

                this.setInstallation(result.installation);
                this.setVersion(result.version);
            });
        }

        getInstallation(): string {
            return this.installation.getInstallation();
        }

        setInstallation(installationText: string): void {
            return this.installation.setInstallation(installationText);
        }

        getVersion(): string {
            return this.version.getVersion();
        }

        setVersion(versionText: string): void {
            return this.version.setVersion(versionText);
        }
    }

}
