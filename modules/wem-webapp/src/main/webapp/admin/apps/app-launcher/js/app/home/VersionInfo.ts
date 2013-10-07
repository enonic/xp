module app_home {

    export class VersionInfo extends api_dom.DivEl {

        private version:string;

        constructor(version:string) {
            super(null, 'version-info');
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
