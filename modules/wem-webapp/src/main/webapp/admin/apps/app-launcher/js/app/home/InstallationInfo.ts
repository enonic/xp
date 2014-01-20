module app.home {

    export class InstallationInfo extends api.dom.DivEl {

        private installation:string;

        constructor(installation:string) {
            super('installation-info');
            this.setInstallation(installation);
        }

        setInstallation(installation:string):void {
            this.installation = installation;
            var text = installation ? ' | ' + installation : '';
            this.getEl().setInnerHtml(text);
        }

        getInstallation():string {
            return this.installation;
        }
    }

}
