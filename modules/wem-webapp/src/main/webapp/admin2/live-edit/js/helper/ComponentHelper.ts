interface ComponentBoxModel {
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

interface ComponentInfo {
    type: String;
    key: String;
    name: String;
    tagName: String;
}

interface ComponentPagePosition {
    top: number;
    left: number;
}

module liveedit {
    export class ComponentHelper {

        static $ = $liveedit;

        public static getBoxModel(component:JQuery):ComponentBoxModel {
            var el = $(component);
            var offset = el.offset();
            var top = offset.top;
            var left = offset.left;
            var width = el.outerWidth();
            var height = el.outerHeight();

            var bt = parseInt(el.css('borderTopWidth'), 10);
            var br = parseInt(el.css('borderRightWidth'), 10);
            var bb = parseInt(el.css('borderBottomWidth'), 10);
            var bl = parseInt(el.css('borderLeftWidth'), 10);

            var pt = parseInt(el.css('paddingTop'), 10);
            var pr = parseInt(el.css('paddingRight'), 10);
            var pb = parseInt(el.css('paddingBottom'), 10);
            var pl = parseInt(el.css('paddingLeft'), 10);

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


        public static getPagePositionForComponent(component:JQuery):ComponentPagePosition {
            var pos = $(component).position();
            return {
                top: pos.top,
                left: pos.left
            };
        }


        public static getComponentInfo(component:JQuery):ComponentInfo {
            return {
                type: getComponentType(component),
                key: getComponentKey(component),
                name: getComponentName(component),
                tagName: getTagNameForComponent(component)
            };
        }


        public static getComponentType(component:JQuery):String {
            return component.data('live-edit-type');
        }


        public static getComponentKey(component:JQuery):String {
            return component.data('live-edit-key');
        }


        public static getComponentName(component:JQuery):String {
            return component.data('live-edit-name') || '[No Name]';
        }


        public static getTagNameForComponent(component:JQuery):String {
            return component[0].tagName.toLowerCase();
        }


        public static supportsTouch():Boolean {
            return document.hasOwnProperty('ontouchend');
        }

    }
}