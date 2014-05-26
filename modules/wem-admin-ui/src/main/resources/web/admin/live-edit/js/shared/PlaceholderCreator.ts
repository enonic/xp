module LiveEdit {

    import RegionItemType = api.liveedit.RegionItemType;
    import RegionView = api.liveedit.RegionView;
    import ItemView = api.liveedit.ItemView;

    export class PlaceholderCreator {

        /** jQuery Sortable placeholder */

        public static createPlaceholderForJQuerySortable(component: ItemView): string {

            var html: string;

            var componentInfoText: string = component.getType().getShortName() + ': ' + component.getName();

            html = 'Drop component here' +
                   '<div style="font-size: 11px;">' + componentInfoText + '</div>';

            return html;
        }

        /** Region. We should have a dedicated class for this  */

        public static renderEmptyRegionPlaceholders(): void {

            var allRegionElements: JQuery = wemjq(RegionItemType.get().getConfig().getCssSelector());
            var region: JQuery;
            var regionComponent: RegionView;
            var regionIsEmpty: Boolean;

            this.removeAllRegionPlaceholders();

            allRegionElements.each((i) => {
                region = wemjq(allRegionElements[i]);
                regionIsEmpty = this.isRegionEmpty(region);
                if (regionIsEmpty) {
                    regionComponent = RegionView.fromJQuery(region);

                    region.append(this.createEmptyRegionPlaceholder(regionComponent));
                }
            });
        }

        public static createEmptyRegionPlaceholder(regionView: RegionView): string {

            var html: string;

            var componentTypeInfoText: string = regionView.getType().getShortName() + ': ' + regionView.getName();

            html = '<div class="live-edit-empty-region-placeholder">' +
                   '    <div>Drag components here</div>' +
                   '    <div style="font-size: 11px;">' + componentTypeInfoText + '</div>' +
                   '</div>';

            return html;
        }

        private static isRegionEmpty(regionElement: JQuery): Boolean {

            var hasNotParts: Boolean = regionElement.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            var hasNotDropTargetPlaceholder: Boolean = regionElement.children('.live-edit-drop-target-placeholder').length === 0;
            return hasNotParts && hasNotDropTargetPlaceholder;
        }

        private static removeAllRegionPlaceholders(): void {
            wemjq('.live-edit-empty-region-placeholder').remove();
        }

    }
}
