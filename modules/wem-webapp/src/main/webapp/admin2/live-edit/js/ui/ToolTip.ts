interface ToolTipPosition {
    x: number;
    y: number;
}

module LiveEdit.ui {
    var $ = $liveEdit;

    var componentHelper = LiveEdit.ComponentHelper;
    var domHelper = LiveEdit.DomHelper;

    export class ToolTip extends LiveEdit.ui.Base {
        private OFFSET_X = 0;
        private OFFSET_Y = 18;

        constructor() {
            super();
            this.addView();
            this.attachEventListeners();
            this.registerGlobalListeners();

            console.log('ToolTip instantiated. Using jQuery ' + $().jquery);
        }

        private registerGlobalListeners():void {
            $(window).on('select.liveEdit.component', () => this.hide());
        }

        private addView():void {
            var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                '    <span class="live-edit-tool-tip-name-text"></span>' +
                '    <span class="live-edit-tool-tip-type-text"></span> ' +
                '</div>';

            this.createElement(html);
            this.appendTo($('body'));
        }

        private  setText(componentType:string, componentName:string):void {
            var $tooltip = this.getRootEl();
            $tooltip.children('.live-edit-tool-tip-type-text').text(componentType);
            $tooltip.children('.live-edit-tool-tip-name-text').text(componentName);
        }

        private attachEventListeners():void {

            $(document).on('mousemove', '[data-live-edit-type]', (event) => {
                var targetIsUiComponent = $(event.target).is('[id*=live-edit-ui-cmp]') ||
                    $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;

                // TODO: Use PubSub instead of calling DragDrop object.
                var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                if (targetIsUiComponent || pageHasComponentSelected || LiveEdit.DragDropSort.isDragging()) {
                    this.hide();
                    return;
                }

                var $component = $(event.target).closest('[data-live-edit-type]');
                var componentInfo = componentHelper.getComponentInfo($component);
                var pos = this.getPosition(event);

                this.getRootEl().css({
                    top: pos.y,
                    left: pos.x
                });

                this.setText(componentInfo.type, componentInfo.name);
            });

            $(document).on('hover', '[data-live-edit-type]', (event) => {
                if (event.type === 'mouseenter') {
                    this.getRootEl().hide(null).fadeIn(300);
                }
            });

            $(document).on('mouseout', () => this.hide());
        }

        private getPosition(event:JQueryEventObject):ToolTipPosition {
            var pageX = event.pageX;
            var pageY = event.pageY;
            var x = pageX + this.OFFSET_X;
            var y = pageY + this.OFFSET_Y;
            var viewPortSize = domHelper.getViewPortSize();
            var scrollTop = domHelper.getDocumentScrollTop();
            var toolTipWidth = this.getRootEl().width();
            var toolTipHeight = this.getRootEl().height();

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

        private hide():void {
            this.getRootEl().css({
                top: '-5000px',
                left: '-5000px'
            });
        }

    }
}