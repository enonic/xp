module LiveEdit.ui {

    // Uses
    var $ = $liveEdit;

    export class Highlighter extends LiveEdit.ui.Base {

        private selectedComponent:LiveEdit.component.Component = null;

        constructor() {
            super();
            this.addView();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners():void {
            $(window).on('mouseOverComponent.liveEdit', (event, component)  => this.onMouseOverComponent(component));
            $(window).on('selectComponent.liveEdit', (event, component)     => this.onSelectComponent(component));
            $(window).on('deselectComponent.liveEdit', ()                   => this.onDeselectComponent());
            $(window).on('mouseOutComponent.liveEdit', ()                   => this.hide());
            $(window).on('sortableStart.liveEdit', ()                       => this.hide());
            $(window).on('componentRemoved.liveEdit', ()                    => this.hide());
            $(window).on('editParagraphComponent.liveEdit', ()              => this.hide());
            $(window).on('resizeBrowserWindow.liveEdit', ()                 => this.handleWindowResize());

            // The component should be re-selected after drag'n drop
            $(window).on('sortstop.liveedit.component', (event, uiEvent, ui, wasSelectedOnDragStart) => {
                if (wasSelectedOnDragStart) {
                    var component = new LiveEdit.component.Component(ui.item);
                    LiveEdit.Selection.select(component);
                }
            });
        }

        private addView():void {
            // Needs to be a SVG element as the css has pointer-events:none
            // CSS pointer-events only works for SVG in IE
            var html:string = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                              '    <rect width="150" height="150"/>' +
                              '</svg>';

            this.createHtmlFromString(html);
            this.appendTo($('body'));
        }

        private onMouseOverComponent(component:LiveEdit.component.Component):void {
            this.show();
            this.resizeToComponent(component);
            this.paintBorder(component);
            this.selectedComponent = component;
        }

        private onSelectComponent(component:LiveEdit.component.Component):void {
            this.selectedComponent = component;

            // Highlighter should not be shown when type page is selected
            if (component.getComponentType().getType() == LiveEdit.component.Type.PAGE) {
                this.hide();
                return;
            }

            this.resizeToComponent(component);
            this.paintBorder(component);
            this.show();
        }

        private onDeselectComponent():void {
            LiveEdit.Selection.clearSelection();
            this.selectedComponent = null;
        }

        private paintBorder(component:LiveEdit.component.Component):void {
            var el:JQuery = this.getEl();
            var style = component.getComponentType().getHighlighterStyle();

            el.css(style);
        }

        private resizeToComponent(component:LiveEdit.component.Component):void {
            var componentBoxModel = component.getElementDimensions();
            var w = Math.round(componentBoxModel.width),
                h = Math.round(componentBoxModel.height),
                top = Math.round(componentBoxModel.top),
                left = Math.round(componentBoxModel.left);

            var highlighter = this.getEl(),
                HighlighterRect = highlighter.find('rect');

            highlighter.width(w);
            highlighter.height(h);
            HighlighterRect.attr('width', w);
            HighlighterRect.attr('height', h);
            highlighter.css({
                top: top,
                left: left
            });
        }

        private show():void {
            this.getEl().show(null);
        }

        private hide():void {
            this.getEl().hide(null);
        }

        private handleWindowResize():void {
            if (this.selectedComponent) {
                this.resizeToComponent(this.selectedComponent);
                this.paintBorder(this.selectedComponent);
            }
        }

    }
}
