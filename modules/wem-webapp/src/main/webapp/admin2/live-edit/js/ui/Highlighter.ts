module LiveEdit.ui {
    var $ = $liveEdit;

    var componentHelper = LiveEdit.component.ComponentHelper;

    export class Highlighter extends LiveEdit.ui.Base {

        private selectedComponent:JQuery = null;

        constructor() {
            super();
            this.addView();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOverComponent.liveEdit', (event, component) => this.componentMouseOver(component));
            $(window).on('selectComponent.liveEdit', (event, component)    => this.selectComponent(component));
            $(window).on('deselectComponent.liveEdit', ()                  => this.deselect());
            $(window).on('mouseOutComponent.liveEdit', ()                  => this.hide());
            $(window).on('sortableStart.liveEdit', ()                 => this.hide());
            $(window).on('removeComponent.liveEdit', ()                    => this.hide());
            $(window).on('editParagraphComponent.liveEdit', ()             => this.hide());
            $(window).on('resizeBrowserWindow.liveEdit', ()                       => this.handleWindowResize());

            $(window).on('sortstop.liveedit.component', (event, uiEvent, ui, wasSelectedOnDragStart) => {
                if (wasSelectedOnDragStart) {
                    $(window).trigger('selectComponent.liveEdit', [ui.item]);
                }
            });
        }

        private addView():void {
            var html:string = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                              '    <rect width="150" height="150"/>' +
                              '</svg>';

            this.createElementsFromString(html);
            this.appendTo($('body'));
        }

        private componentMouseOver(component:JQuery):void {
            this.show();
            this.paintBorder(component);
            this.selectedComponent = component;
        }

        private selectComponent(component:JQuery):void {
            this.selectedComponent = component;
            var componentType = componentHelper.getComponentType(component);

            component.addClass('live-edit-selected-component');

            // Highlighter should not be shown when type page is selected
            if (componentType === 'page') {
                this.hide();
                return;
            }

            this.paintBorder(component);
            this.show();
        }

        private deselect():void {
            $('.live-edit-selected-component').removeClass('live-edit-selected-component');
            this.selectedComponent = null;
        }

        private paintBorder(component):void {
            var border = this.getRootEl();

            this.resizeBorderToComponent(component);

            var style = componentHelper.getHighlighterStyleForComponent(component);
            border.css('stroke', style.strokeColor);
            border.css('fill', style.fillColor);
            border.css('stroke-dasharray', style.strokeDashArray);
        }

        private resizeBorderToComponent(component):void {
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

        private show():void {
            this.getRootEl().show(null);
        }

        private hide():void {
            this.getRootEl().hide(null);
        }

        private handleWindowResize():void {
            if (this.selectedComponent) {
                this.paintBorder(this.selectedComponent);
            }
        }

    }
}
