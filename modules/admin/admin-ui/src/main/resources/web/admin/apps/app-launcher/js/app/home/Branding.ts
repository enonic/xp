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
                new api.system.StatusRequest().sendAndParse().then((status: api.system.StatusResult) => {
                    this.setInstallation(status.getInstallation());
                }).done();
            }
        }

        private setInstallation(installationText: string): void {
            this.installation.setHtml(installationText);
        }

    }

}
