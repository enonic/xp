module api.liveedit {


    export class RegionPlaceholder extends api.dom.DivEl {

        private regionView: RegionView;

        constructor(regionView: RegionView) {
            super("region-placeholder");
            this.regionView = regionView;


            var dragComponentsHereEl = new api.dom.PEl();
            dragComponentsHereEl.setHtml("Drop components here");

            var componentTypeInfoText: string = api.util.StringHelper.capitalize(regionView.getType().getShortName()) + ': ' +
                                                regionView.getName();
            var typeInfoEl = new api.dom.PEl();
            typeInfoEl.setHtml(componentTypeInfoText);

            this.appendChild(dragComponentsHereEl);
            this.appendChild(typeInfoEl);
        }
    }
}