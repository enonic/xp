module app.home {

    export class Branding extends api.dom.DivEl {

        private installation: api.dom.DivEl;

        private productHeader: api.dom.H1El;

        constructor(loadInstallation?: boolean) {
            super('branding');

            this.productHeader = new api.dom.H1El();
            this.productHeader.setHtml('Enonic experience platform');
            this.installation = new api.dom.DivEl("installation-info");

            this.appendChild(this.productHeader);
            this.appendChild(this.installation);

            if (loadInstallation) {
                new api.system.StatusRequest().send().done((response: api.rest.JsonResponse<api.system.StatusJson>) => {
                    this.setInstallation(response.getResult().installation);
                });
            }
        }

        getInstallation(): string {
            return this.installation.getHtml();
        }

        setInstallation(installationText: string): void {
            this.installation.setHtml(installationText);
        }

    }

}
