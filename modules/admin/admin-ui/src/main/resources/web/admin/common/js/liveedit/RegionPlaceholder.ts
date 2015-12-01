module api.liveedit {

    import Region = api.content.page.region.Region;

    export class RegionPlaceholder extends ItemViewPlaceholder {

        private region: Region;

        constructor(region: Region) {
            super();
            this.addClassEx("region-placeholder");

            this.region = region;

            var dragComponentsHereEl = new api.dom.PEl();
            dragComponentsHereEl.setHtml("Drop here");

            this.appendChild(dragComponentsHereEl);
        }
    }
}