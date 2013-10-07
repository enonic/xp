module LiveEdit {

    // Uses
    var $ = $liveEdit;

    export class PlaceholderCreator {

        /** jQuery Sortable placeholder */

        public static createPlaceholderForJQuerySortable(component:LiveEdit.component.Component):string {

            var html:string;

            var componentInfoText:string = component.getComponentType().getName() + ': ' +  component.getName();

            html = 'Drop component here' +
                   '<div style="font-size: 11px;">' + componentInfoText + '</div>';

            return html;
        }

        /** Region. We should have a dedicated class for this  */

        public static renderEmptyRegionPlaceholders():void {

            var allRegionElements:JQuery = $(LiveEdit.component.TypeConfiguration[LiveEdit.component.Type.REGION].cssSelector);
            var region:JQuery;
            var regionComponent:LiveEdit.component.Component;
            var regionIsEmpty:Boolean;

            this.removeAllRegionPlaceholders();

            allRegionElements.each((i) => {
                region = $(allRegionElements[i]);
                regionIsEmpty = this.isRegionEmpty(region);
                if (regionIsEmpty) {
                    regionComponent = new LiveEdit.component.Component(region);

                    region.append(this.createEmptyRegionPlaceholder(regionComponent));
                }
            });
        }

        public static createEmptyRegionPlaceholder (regionComponent:LiveEdit.component.Component):string {

            var html:string;

            var componentTypeInfoText:string = regionComponent.getComponentType().getName() +': '+ regionComponent.getName();

            html = '<div class="live-edit-empty-region-placeholder">' +
                   '    <div>Drag components here</div>' +
                   '    <div style="font-size: 11px;">' + componentTypeInfoText + '</div>' +
                   '</div>';

            return html;
        }

        private static isRegionEmpty(regionElement:JQuery):Boolean {

            var hasNotParts:Boolean = regionElement.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            var hasNotDropTargetPlaceholder:Boolean = regionElement.children('.live-edit-drop-target-placeholder').length === 0;
            return hasNotParts && hasNotDropTargetPlaceholder;
        }

        private static removeAllRegionPlaceholders():void {
            $('.live-edit-empty-region-placeholder').remove();
        }

    }
}
