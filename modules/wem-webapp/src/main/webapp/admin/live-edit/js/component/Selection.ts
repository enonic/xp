module LiveEdit.component {

    // Uses
    var $ = $liveEdit;

    export var ATTRIBUTE_NAME:string = 'data-live-edit-selected';


    export class Selection {
        static COMPONENT_ATTR:string = "data-live-edit-component";
        static REGION_ATTR:string = "data-live-edit-region";

        public static handleSelect(element:HTMLElement, event?:JQueryEventObject) {
            var component = Component.fromElement(element);

            if (Selection.getType(element) == "page") {
                $(element).trigger('pageSelect.liveEdit');
            } else if (Selection.getType(element) == "region") {
                $(element).trigger('regionSelect.liveEdit', element.getAttribute(Selection.REGION_ATTR));
            } else if (Selection.getType(element) == "component") {
                $(element).trigger('componentSelect.liveEdit', element.getAttribute(Selection.COMPONENT_ATTR));
            }

            this.setSelectionAttributeOnElement($(element));

            var mouseClickPagePosition:any = null;
            if (event && !component.isEmpty()) {
                mouseClickPagePosition = {
                    x: event.pageX,
                    y: event.pageY
                };
            }

            $(window).trigger('selectComponent.liveEdit', [Component.fromElement(element), mouseClickPagePosition]);
        }

        public static getType(element:HTMLElement):string {
            if (element.hasAttribute(Selection.COMPONENT_ATTR) || element.getAttribute('data-live-edit-empty-component') == "true") {
                return "component";
            } else if (element.hasAttribute(Selection.REGION_ATTR)) {
                return "region";
            } else if (element.tagName.toLocaleLowerCase() === "body") {
                return "page";
            }
            return null;
        }

        public static deselect():void {
            $(window).trigger('deselectComponent.liveEdit');
            $(window).trigger('componentDeselect.liveEdit');
            this.removeSelectedAttribute();
        }

        public static setSelectionAttributeOnElement(element:JQuery):void {
            this.removeSelectedAttribute();
            element.attr(ATTRIBUTE_NAME, 'true');
        }

        public static getSelectedComponent():Component {
            try {
                return Component.fromJQuery($('[' + ATTRIBUTE_NAME + ']'));

            } catch (ex) {
                return null;
            }
        }

        public static pageHasSelectedElement():boolean {
            return $('[' + ATTRIBUTE_NAME + ']').length > 0;
        }

        public static removeSelectedAttribute():void {
            $('[' + ATTRIBUTE_NAME + ']').removeAttr(ATTRIBUTE_NAME);
        }

    }
}