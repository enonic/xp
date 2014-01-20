module app.home {

    export class VersionInfo extends api.dom.DivEl {

        private version:string;

        constructor(version:string) {
            super('version-info');
        }

        setVersion(version:string):void {
            this.version = version;
            this.getEl().setInnerHtml(version);
        }

        getVersion():string {
            return this.version;
        }
    }

}
