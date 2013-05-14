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
    type: string;
    key: string;
    name: string;
    tagName: string;
}

interface ComponentPagePosition {
    top: number;
    left: number;
}

module LiveEdit {
    export class ComponentHelper {

        static $:JQuery = $liveedit;

        public static getBoxModel(component:JQuery):ComponentBoxModel {
            var cmp:JQuery = component;
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


        public static getPagePositionForComponent(component:JQuery):ComponentPagePosition {
            var pos = component.position();
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


        public static getComponentType(component:JQuery):string {
            return component.data('live-edit-type');
        }


        public static getComponentKey(component:JQuery):string {
            return component.data('live-edit-key');
        }


        public static getComponentName(component:JQuery):string {
            return component.data('live-edit-name') || '[No Name]';
        }


        public static getTagNameForComponent(component:JQuery):string {
            return component[0].tagName.toLowerCase();
        }


        public static supportsTouch():Boolean {
            return document.hasOwnProperty('ontouchend');
        }

    }
}