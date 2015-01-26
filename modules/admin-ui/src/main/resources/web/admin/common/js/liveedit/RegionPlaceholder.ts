module api.liveedit {

    import Region = api.content.page.region.Region;

    export class RegionPlaceholder extends ItemViewPlaceholder {

        private region: Region;

        constructor(region: Region) {
            super();
            this.addClass("region-placeholder");

            this.region = region;

            var dragComponentsHereEl = new api.dom.PEl();
            dragComponentsHereEl.setHtml("Drop components here");

            var componentTypeInfoText: string = api.util.StringHelper.capitalize(RegionItemType.get().getShortName()) + ': ' +
                                                region.getName();
            var typeInfoEl = new api.dom.PEl();
            typeInfoEl.setHtml(componentTypeInfoText);

            this.appendChildren(dragComponentsHereEl, typeInfoEl);
        }
    }
}