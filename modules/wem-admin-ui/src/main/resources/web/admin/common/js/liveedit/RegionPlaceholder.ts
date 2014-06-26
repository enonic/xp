module api.liveedit {


    export class RegionPlaceholder extends api.dom.DivEl {

        private regionView: RegionView;

        constructor(regionView: RegionView) {
            super("live-edit-empty-region-placeholder");
            this.regionView = regionView;


            var dragComponentsHereEl = new api.dom.PEl();
            dragComponentsHereEl.setText("Drop components here");

            var componentTypeInfoText: string = api.util.capitalize(regionView.getType().getShortName()) + ': ' + regionView.getName();
            var typeInfoEl = new api.dom.PEl();
            typeInfoEl.setText(componentTypeInfoText);

            this.appendChild(dragComponentsHereEl);
            this.appendChild(typeInfoEl);
        }
    }
}