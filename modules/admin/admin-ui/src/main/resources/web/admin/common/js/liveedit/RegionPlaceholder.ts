module api.liveedit {

    import Region = api.content.page.region.Region;
    import i18n = api.util.i18n;

    export class RegionPlaceholder extends ItemViewPlaceholder {

        private region: Region;

        constructor(region: Region) {
            super();
            this.addClassEx('region-placeholder');

            this.region = region;

            let dragComponentsHereEl = new api.dom.PEl();
            dragComponentsHereEl.setHtml(i18n('live.view.drag.drophere'));

            this.appendChild(dragComponentsHereEl);
        }
    }
}
