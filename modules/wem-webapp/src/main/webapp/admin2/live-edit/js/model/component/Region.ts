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


        private registerGlobalListeners() {
            $(window).on('component.onSortUpdate component.onSortOver component.onRemove', () => {
                this.renderEmptyPlaceholders();
            });
        }


        private renderEmptyPlaceholders() {
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


        private appendEmptyPlaceholder($region) {
            var html = '<div>Drag components here</div>';
            html += '<div style="font-size: 10px;">' + componentHelper.getComponentName($region) + '</div>';
            var $placeholder = $('<div/>', {
                'class': 'live-edit-empty-region-placeholder',
                'html': html
            });
            $region.append($placeholder);
        }


        private isRegionEmpty($region) {
            var hasNotParts = $region.children('[data-live-edit-type]' + ':not(:hidden)').length === 0;
            var hasNotDropTargetPlaceholder = $region.children('.live-edit-drop-target-placeholder').length === 0;
            return hasNotParts && hasNotDropTargetPlaceholder;
        }


        private removeAllRegionPlaceholders() {
            $('.live-edit-empty-region-placeholder').remove();
        }

    }
}
