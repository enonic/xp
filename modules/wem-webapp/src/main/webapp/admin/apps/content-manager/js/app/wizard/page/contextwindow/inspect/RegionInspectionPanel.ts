module app.wizard.page.contextwindow.inspect {

    import Region = api.content.page.region.Region;

    export class RegionInspectionPanel extends BaseInspectionPanel {

        private region: Region;

        constructor() {
            super("live-edit-font-icon-region");
        }

        setRegion(region: Region) {
            this.region = region;
            if (region) {
                this.setMainName(region.getName() );
                this.setSubName(region.getPath().toString());
            } else {
                this.setMainName("[No  Region given]" );
                this.setSubName("");
            }
        }

    }
}
