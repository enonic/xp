module app.contextwindow.inspect {

    export class RegionInspectionPanel extends BaseInspectionPanel {

        private region: api.content.page.region.Region;

        constructor() {
            super("live-edit-font-icon-region");
        }

        setRegion(region: api.content.page.region.Region) {
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
