module LiveEdit.ui {
    var $ = $liveedit;

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

        private registerGlobalListeners() {
            $(window).on('component.onSelect', () => {
                this.hide();
            });
        }


        private addView() {
            var html = '<div class="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                '    <span class="live-edit-tool-tip-name-text"></span>' +
                '    <span class="live-edit-tool-tip-type-text"></span> ' +
                '</div>';

            this.createElement(html);
            this.appendTo($('body'));
        }


        private  setText(componentType, componentName) {
            var $tooltip = this.getEl();
            $tooltip.children('.live-edit-tool-tip-type-text').text(componentType);
            $tooltip.children('.live-edit-tool-tip-name-text').text(componentName);
        }


        private attachEventListeners() {

            $(document).on('mousemove', '[data-live-edit-type]', (event) => {
                var targetIsUiComponent = $(event.target).is('[id*=live-edit-ui-cmp]') ||
                    $(event.target).parents('[id*=live-edit-ui-cmp]').length > 0;

                // TODO: Use PubSub instead of calling DragDrop object.
                var pageHasComponentSelected = $('.live-edit-selected-component').length > 0;
                if (targetIsUiComponent || pageHasComponentSelected || AdminLiveEdit.DragDropSort.isDragging()) {
                    this.hide();
                    return;
                }

                var $component = $(event.target).closest('[data-live-edit-type]');
                var componentInfo = componentHelper.getComponentInfo($component);
                var pos = this.getPosition(event);

                this.getEl().css({
                    top: pos.y,
                    left: pos.x
                });

                this.setText(componentInfo.type, componentInfo.name);
            });

            $(document).on('hover', '[data-live-edit-type]', (event) => {
                if (event.type === 'mouseenter') {
                    this.getEl().hide().fadeIn(300);
                }
            });

            $(document).on('mouseout', () => {
                this.hide();
            });
        }


        getPosition(event) {
            var pageX = event.pageX;
            var pageY = event.pageY;
            var x = pageX + this.OFFSET_X;
            var y = pageY + this.OFFSET_Y;
            var viewPortSize = domHelper.getViewPortSize();
            var scrollTop = domHelper.getDocumentScrollTop();
            var toolTipWidth = this.getEl().width();
            var toolTipHeight = this.getEl().height();

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


        hide() {
            this.getEl().css({
                top: '-5000px',
                left: '-5000px'
            });
        }

    }
}