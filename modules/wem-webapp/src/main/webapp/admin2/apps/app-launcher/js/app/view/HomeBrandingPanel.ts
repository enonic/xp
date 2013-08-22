///<reference path='InstallationInfoPanel.ts' />
///<reference path='VersionInfoPanel.ts' />

module app_view {

    export class HomeBrandingPanel extends api_dom.DivEl {

        private installation:InstallationInfoPanel;
        private version:VersionInfoPanel;

        constructor(installation?:string, version?:string) {
            super(null, 'admin-home-branding');

            installation = installation || '';
            version = version || '';

            this.installation = new InstallationInfoPanel(installation);
            this.version = new VersionInfoPanel(version);
            this.appendChild(this.installation);
            this.appendChild(this.version);
        }

        getInstallation():string {
            return this.installation.getInstallation();
        }

        setInstallation(installationText:string):void {
            return this.installation.setInstallation(installationText);
        }

        getVersion():string {
            return this.version.getVersion();
        }

        setVersion(versionText:string):void {
            return this.version.setVersion(versionText);
        }
    }

}
