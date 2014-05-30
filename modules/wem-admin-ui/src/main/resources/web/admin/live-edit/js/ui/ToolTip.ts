interface ToolTipPosition {
    x: number;
    y: number;
}

module LiveEdit.ui {

    import ItemView = api.liveedit.ItemView;
    import PageComponentSelectComponentEvent = api.liveedit.PageComponentSelectComponentEvent;
    
    var domHelper = LiveEdit.DomHelper;

    export class ToolTip extends LiveEdit.ui.Base {
        OFFSET_X = 10;
        OFFSET_Y = 10;

        constructor() {
            super();
            this.addView();
            this.attachEventListeners();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners(): void {
            PageComponentSelectComponentEvent.on(() => this.hide());
        }

        private addView(): void {
            var html: string = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                               '    <span class="live-edit-tool-tip-name-text"></span>' +
                               '    <span class="live-edit-tool-tip-type-text"></span> ' +
                               '</div>';

            this.createHtmlFromString(html);
            this.appendTo(wemjq('body'));
        }

        private setText(itemView: ItemView): void {
            var tooltip: JQuery = this.getEl();

            tooltip.children('.live-edit-tool-tip-type-text').text(itemView.getType().getShortName());
            tooltip.children('.live-edit-tool-tip-name-text').text(itemView.getName());
        }

        private attachEventListeners(): void {
            wemjq(document).on('mousemove', '[data-live-edit-type]', (event) => {

                // fixme: Use PubSub instead of calling DragDrop object.
                if (LiveEdit.component.Selection.pageHasSelectedElement() || LiveEdit.component.dragdropsort.DragDropSort.isDragging()) {
                    this.hide();
                    return;
                }

                var pos = this.getPositionFromEvent(event);

                this.getEl().css({
                    top: pos.y,
                    left: pos.x
                });

            });

            wemjq(document).on('mouseover', '[data-live-edit-type]', (event) => {

                var itemView: ItemView = ItemView.fromJQuery(wemjq(event.target).closest('[data-live-edit-type]'));
                this.setText(itemView);

                this.getEl().hide(null).show();
            });

            wemjq('[data-live-edit-type]').mouseleave(() => {
                this.getEl().hide(null);
            });
        }

        private getPositionFromEvent(event: JQueryEventObject) {
            var pageX = event.pageX,
                pageY = event.pageY,
                x = pageX + this.OFFSET_X,
                y = pageY + this.OFFSET_Y,
                viewPortSize = domHelper.getViewPortSize(),
                scrollTop = domHelper.getDocumentScrollTop(),
                toolTipWidth = this.getEl().width(),
                toolTipHeight = this.getEl().height();

            if (x + toolTipWidth > (viewPortSize.width - this.OFFSET_X * 2) - 50) {
                x = pageX - toolTipWidth - (this.OFFSET_X * 2);
            }
            if (y + toolTipHeight > (viewPortSize.height + scrollTop - this.OFFSET_Y * 2)) {
                y = pageY - toolTipHeight - (this.OFFSET_Y * 2);
            }
            return {
                x: x,
                y: y
            };
        }

        private hide(): void {
            this.getEl().css({
                top: '-5000px',
                left: '-5000px'
            });
        }

    }
}