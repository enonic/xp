module LiveEdit.model {
    var $ = $liveedit;
    var componentHelper = LiveEdit.ComponentHelper;

    export class Region extends LiveEdit.model.Base {
        constructor() {
            super();

            this.cssSelector = '[data-live-edit-type=region]';

            this.renderEmptyPlaceholders();
            this.attachMouseOverEvent();
            this.attachMouseOutEvent();
            this.attachClickEvent();
            this.registerGlobalListeners();

            console.log('Region model instantiated. Using jQuery ' + $().jquery);
        }


        private registerGlobalListeners():void {
            $(window).on('sortUpdate.liveEdit.component sortOver.liveEdit.component remove.liveEdit.component', () => {
                this.renderEmptyPlaceholders();
            });
        }


        private renderEmptyPlaceholders():void {
            this.removeAllRegionPlaceholders();
            var regions = this.getAll(),
                region:JQuery;

            regions.each((i) => {
                region = $(regions[i]);
                var regionIsEmpty = this.isRegionEmpty(region);
                if (regionIsEmpty) {
                    this.appendEmptyPlaceholder(region);
                }
            });
        }


        private appendEmptyPlaceholder(region:JQuery):void {
            var html = '<div>Drag components here</div>';
            html += '<div style="font-size: 10px;">' + componentHelper.getComponentName(region) + '</div>';
            var $placeholder = $('<div/>', {
                'class': 'live-edit-empty-region-placeholder',
                'html': html
            });
            region.append($placeholder);
        }


        private isRegionEmpty(region:JQuery):Boolean {
            var hasNotParts:Boolean = region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            var hasNotDropTargetPlaceholder:Boolean = region.children('.live-edit-drop-target-placeholder').length === 0;
            return hasNotParts && hasNotDropTargetPlaceholder;
        }


        private removeAllRegionPlaceholders():void {
            $('.live-edit-empty-region-placeholder').remove();
        }

    }
}
