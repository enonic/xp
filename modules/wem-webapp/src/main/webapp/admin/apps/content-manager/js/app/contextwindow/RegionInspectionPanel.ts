module app.contextwindow {

    export class RegionInspectionPanel extends BaseInspectionPanel {

        private region: api.content.page.region.Region;

        constructor() {
            super("live-edit-font-icon-region");
        }

        setRegion(region: api.content.page.region.Region) {
            this.region = region;
            if (region) {
                this.setName(region.getName(), region.getPath().toString());
            } else {
                this.setName('[No Name]', 'region');
            }
        }

    }
}
