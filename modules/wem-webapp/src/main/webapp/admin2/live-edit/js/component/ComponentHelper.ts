interface ElementDimensions {
    top: number;
    left: number;
    width: number;
    height: number;
    borderTop: number;
    borderRight: number;
    borderBottom: number;
    borderLeft: number;
    paddingTop: number;
    paddingRight: number;
    paddingBottom: number;
    paddingLeft: number;
}

module LiveEdit.component {
    export class ComponentHelper {

        static $:JQuery = $liveEdit;

        public static getDimensionsFromElement(componentElement:JQuery):ElementDimensions {
            var cmp:JQuery = componentElement;
            var offset = cmp.offset();
            var top = offset.top;
            var left = offset.left;
            var width = cmp.outerWidth();
            var height = cmp.outerHeight();

            var bt = parseInt(cmp.css('borderTopWidth'), 10);
            var br = parseInt(cmp.css('borderRightWidth'), 10);
            var bb = parseInt(cmp.css('borderBottomWidth'), 10);
            var bl = parseInt(cmp.css('borderLeftWidth'), 10);

            var pt = parseInt(cmp.css('paddingTop'), 10);
            var pr = parseInt(cmp.css('paddingRight'), 10);
            var pb = parseInt(cmp.css('paddingBottom'), 10);
            var pl = parseInt(cmp.css('paddingLeft'), 10);

            return {
                top: top,
                left: left,
                width: width,
                height: height,
                borderTop: bt,
                borderRight: br,
                borderBottom: bb,
                borderLeft: bl,
                paddingTop: pt,
                paddingRight: pr,
                paddingBottom: pb,
                paddingLeft: pl
            };
        }

        public static getComponentTypeFromElement(componentElement:JQuery):string {
            return componentElement.data('live-edit-type');
        }

        public static getComponentKeyFromElement(componentElement:JQuery):string {
            return componentElement.data('live-edit-key');
        }

        public static getComponentName(componentElement:JQuery):string {
            return componentElement.data('live-edit-name') || '[No Name]';
        }

    }
}