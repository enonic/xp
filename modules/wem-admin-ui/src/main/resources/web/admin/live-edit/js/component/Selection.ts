module LiveEdit.component {

    import ComponentPath = api.content.page.ComponentPath;
    import PageSelectEvent = api.liveedit.PageSelectEvent;
    import RegionSelectEvent = api.liveedit.RegionSelectEvent;
    import ComponentSelectEvent = api.liveedit.PageComponentSelectEvent;

    // Uses
    var $ = $liveEdit;

    export var ATTRIBUTE_NAME: string = 'data-live-edit-selected';


    export class Selection {
        static COMPONENT_ATTR: string = "data-live-edit-component";
        static REGION_ATTR: string = "data-live-edit-region";

        public static handleSelect(element: HTMLElement, event?: JQueryEventObject, waitForRender: boolean = false) {

            var component = Component.fromElement(element);

            if (Selection.getType(element) == "page") {

                new PageSelectEvent().fire();
            }
            else if (Selection.getType(element) == "region") {

                var regionPath = element.getAttribute(Selection.REGION_ATTR);
                if (regionPath && regionPath.length > 0) {
                    new RegionSelectEvent(api.content.page.RegionPath.fromString(regionPath)).fire();
                }
            }
            else if (Selection.getType(element) == "component") {

                new ComponentSelectEvent(ComponentPath.fromString(element.getAttribute(Selection.COMPONENT_ATTR)), component).fire();
            }

            this.setSelectionAttributeOnElement($(element));

            var mouseClickPagePosition: any = null;
            if (event && !component.isEmpty()) {
                mouseClickPagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            }

            if (waitForRender) {
                var maxIterations = 10;
                var iterations = 0;
                var interval = setInterval(() => {
                    if (element.offsetHeight > 0) {
                        $(window).trigger('selectComponent.liveEdit', [Component.fromElement(element), mouseClickPagePosition]);
                        clearInterval(interval);
                    }
                    iterations++;
                    if (iterations >= maxIterations) {
                        clearInterval(interval);
                    }
                }, 300);
            } else {
                $(window).trigger('selectComponent.liveEdit', [Component.fromElement(element), mouseClickPagePosition]);
            }


        }

        public static getType(element: HTMLElement): string {
            if (element.hasAttribute(Selection.COMPONENT_ATTR) || element.getAttribute('data-live-edit-empty-component') == "true") {
                return "component";
            } else if (element.hasAttribute(Selection.REGION_ATTR)) {
                return "region";
            } else if (element.tagName.toLocaleLowerCase() === "body") {
                return "page";
            }
            return null;
        }

        public static deselect(): void {
            $(window).trigger('deselectComponent.liveEdit');
            $(window).trigger('componentDeselect.liveEdit');
            this.removeSelectedAttribute();
        }

        public static setSelectionAttributeOnElement(element: JQuery): void {
            this.removeSelectedAttribute();
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static pageHasSelectedElement(): boolean {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static removeSelectedAttribute(): void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}