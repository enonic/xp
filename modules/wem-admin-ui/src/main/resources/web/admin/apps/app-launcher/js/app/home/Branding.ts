module app.home {

    export class Branding extends api.dom.DivEl {

        private installation: InstallationInfo;

        private productHeader: ProductHeader;

        constructor(installation?: string, version?: string) {
            super('branding');

            installation = installation || '';
            version = version || '';

            this.productHeader = new ProductHeader();
            this.installation = new InstallationInfo(installation);

            this.appendChild(this.productHeader);
            this.appendChild(this.installation);


            new api.system.StatusRequest().send().done((response: api.rest.JsonResponse<api.system.StatusJson>) => {
                var result = response.getResult();

                this.setInstallation(result.installation);
            });
        }

        getInstallation(): string {
            return this.installation.getInstallation();
        }

        setInstallation(installationText: string): void {
            return this.installation.setInstallation(installationText);
        }

    }

}
