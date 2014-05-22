module LiveEdit.ui {

    import ItemView = api.liveedit.ItemView;
    import SortableStartEvent = api.liveedit.SortableStartEvent;
    import PageComponentDeselectEvent = api.liveedit.PageComponentDeselectEvent;
    import PageComponentRemoveEvent = api.liveedit.PageComponentRemoveEvent;

    // Uses
    var $ = $liveEdit;

    export class Highlighter extends LiveEdit.ui.Base {

        private selectedComponent: ItemView = null;

        constructor() {
            super();
            this.addView();
            this.registerGlobalListeners();
        }

        private registerGlobalListeners(): void {
            $(window).on('mouseOverComponent.liveEdit', (event, component: ItemView)  => this.onMouseOverComponent(component));
            $(window).on('selectComponent.liveEdit', (event, component: ItemView)     => this.onSelectComponent(component));
            PageComponentDeselectEvent.on(() => this.onDeselectComponent());
            $(window).on('mouseOutComponent.liveEdit', ()                   => this.hide());
            SortableStartEvent.on(() => this.hide());
            PageComponentRemoveEvent.on(() => this.hide());
            $(window).on('editTextComponent.liveEdit', ()                   => this.hide());
            $(window).on('resizeBrowserWindow.liveEdit', ()                 => this.handleWindowResize());

            // The component should be re-selected after drag'n drop
            $(window).on('sortstop.liveedit.component', (event, uiEvent, ui, wasSelectedOnDragStart) => {
                if (wasSelectedOnDragStart) {
                    var itemView = ItemView.fromJQuery(ui.item);
                    LiveEdit.component.Selection.handleSelect(itemView);
                }
            });
        }

        private addView(): void {
            // Needs to be a SVG element as the css has pointer-events:none
            // CSS pointer-events only works for SVG in IE
            var html: string = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="live-edit-highlight-border" style="top:-5000px;left:-5000px">' +
                               '    <rect width="150" height="150"/>' +
                               '</svg>';

            this.createHtmlFromString(html);
            this.appendTo($('body'));
        }

        private onMouseOverComponent(component: ItemView): void {
            this.show();
            this.resizeToComponent(component);
            this.paintBorder(component);
            this.selectedComponent = component;
        }

        private onSelectComponent(component: ItemView): void {
            this.selectedComponent = component;

            // Highlighter should not be shown when type page is selected
            if (component.getType().equals(api.liveedit.PageItemType.get())) {
                this.hide();
                return;
            }

            this.resizeToComponent(component);
            this.paintBorder(component);
            this.show();
        }

        private onDeselectComponent(): void {
            this.hide();
            LiveEdit.component.Selection.removeSelectedAttribute();
            this.selectedComponent = null;
        }

        private paintBorder(component: ItemView): void {
            var el: JQuery = this.getEl();
            var style = component.getType().getConfig().getHighlighterStyle();

            el.css(style);
        }

        private resizeToComponent(component: ItemView): void {
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

        private show(): void {
            this.getEl().show(null);
        }

        private hide(): void {
            this.getEl().hide(null);
        }

        private handleWindowResize(): void {
            if (this.selectedComponent) {
                this.resizeToComponent(this.selectedComponent);
                this.paintBorder(this.selectedComponent);
            }
        }

    }
}
