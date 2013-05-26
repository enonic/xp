module LiveEdit.ui {
    var $ = $liveedit;

    var componentHelper = LiveEdit.ComponentHelper;

    export class Highlighter extends LiveEdit.ui.Base {

        private selectedComponent:JQuery = null;

        constructor() {
            super();
            this.addView();
            this.registerGlobalListeners();

            console.log('Highlighter instantiated. Using jQuery ' + $().jquery);
        }


        private registerGlobalListeners():void {
            $(window).on('mouseOver.liveEdit.component', (event, component) => {
                this.componentMouseOver(component);
            });

            $(window).on('select.liveEdit.component', (event, component) => {
                this.selectComponent(component);
            });

            $(window).on('deselect.liveEdit.component', () => this.deselect());

            $(window).on('mouseOut.liveEdit.component sortStart.liveEdit.component remove.liveEdit.component paragraphEdit.liveEdit.component', () => this.hide());

            $(window).on('resize.liveEdit.window', () => this.handleWindowResize());

            $(window).on('sortstop.liveedit.component', (event, uiEvent, ui, wasSelectedOnDragStart) => {
                if (wasSelectedOnDragStart) {
                    $(window).trigger('select.liveEdit.component', [ui.item]);
                }
            });
        }


        private addView():void {
            var html =  '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                        '    <rect width="150" height="150"/>' +
                        '</svg>';

            this.createElement(html);
            this.appendTo($('body'));
        }


        private componentMouseOver(component:JQuery):void {
            this.show();
            this.paintBorder(component);
        }


        private selectComponent(component:JQuery):void {
            this.selectedComponent = component;
            var componentType = componentHelper.getComponentType(component);

            // Move CSS class manipulation to model base
            $('.live-edit-selected-component').removeClass('live-edit-selected-component');

            component.addClass('live-edit-selected-component');

            // Highlighter should not be shown when type page is selected
            if (componentType === 'page') {
                this.hide();
                return;
            }

            this.paintBorder(component);
            this.show();
        }


        deselect():void {
            $('.live-edit-selected-component').removeClass('live-edit-selected-component');
            this.selectedComponent = null;
        }


        paintBorder(component):void {
            var border = this.getRootEl();

            this.resizeBorderToComponent(component);

            var style = componentHelper.getHighlighterStyleForComponent(component);
            border.css('stroke', style.strokeColor);
            border.css('fill', style.fillColor);
            border.css('stroke-dasharray', style.strokeDashArray);
        }


        resizeBorderToComponent(component):void {
            var componentBoxModel = componentHelper.getBoxModel(component);
            var w = Math.round(componentBoxModel.width),
                h = Math.round(componentBoxModel.height),
                top = Math.round(componentBoxModel.top),
                left = Math.round(componentBoxModel.left);

            var $highlighter = this.getRootEl(),
                $HighlighterRect = $highlighter.find('rect');

            $highlighter.width(w);
            $highlighter.height(h);
            $HighlighterRect.attr('width', w);
            $HighlighterRect.attr('height', h);
            $highlighter.css({
                top: top,
                left: left
            });
        }


        show():void {
            this.getRootEl().show(null);
        }


        hide():void {
            this.getRootEl().hide(null);
        }





        handleWindowResize():void {
            if (this.selectedComponent) {
                this.paintBorder(this.selectedComponent);
            }
        }

    }
}
