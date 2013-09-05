module LiveEdit.component.mouseevent {

    // Uses
    var $ = $liveEdit;

    export class Region extends LiveEdit.component.mouseevent.Base {
        constructor() {
            super();

            this.componentCssSelectorFilter = LiveEdit.component.Configuration[LiveEdit.component.Type.REGION].cssSelector;

            this.renderEmptyPlaceholders();
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('sortableUpdate.liveEdit sortableOver.liveEdit componentRemoved.liveEdit', () => {
                this.renderEmptyPlaceholders();
            });
        }

        // fixme: extract placeholder stuff to another class

        private renderEmptyPlaceholders():void {
            this.removeAllRegionPlaceholders();
            var allRegionElements:JQuery = this.getAll(),
                region:JQuery;

            allRegionElements.each((i) => {
                region = $(allRegionElements[i]);
                var regionIsEmpty = this.isRegionEmpty(region);
                if (regionIsEmpty) {
                    this.appendEmptyPlaceholder(region);
                }
            });
        }

        private appendEmptyPlaceholder(regionElement:JQuery):void {
            var html = '<div>Drag components here</div>';
            var regionComponent = new LiveEdit.component.Component(regionElement);
            html += '<div style="font-size: 10px;">' + regionComponent.getName() + '</div>';
            var placeholderElement:JQuery = $('<div/>', {
                'class': 'live-edit-empty-region-placeholder',
                'html': html
            });
            regionElement.append(placeholderElement);
        }

        private isRegionEmpty(regionElement:JQuery):Boolean {
            var hasNotParts:Boolean = regionElement.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            var hasNotDropTargetPlaceholder:Boolean = regionElement.children('.live-edit-drop-target-placeholder').length === 0;
            return hasNotParts && hasNotDropTargetPlaceholder;
        }

        private removeAllRegionPlaceholders():void {
            $('.live-edit-empty-region-placeholder').remove();
        }

    }
}
