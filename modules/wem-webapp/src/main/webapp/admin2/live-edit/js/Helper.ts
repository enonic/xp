interface BoxModel {
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

interface DocumentSize {
    width: number;
    height: number;
}

interface ViewportSize {
    width: number;
    height: number;
}

interface ComponentInfo {
    type: String;
    key: String;
    name: String;
    tagName: String;
}

module liveedit {
    export class Helper {

        static $ = $liveedit;


        static getDocumentSize():DocumentSize {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        }


        static getViewPortSize():ViewportSize {
            var $window = $(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        }


        static getDocumentScrollTop():number {
            return $(document).scrollTop();
        }


        static getBoxModel(component:JQuery):BoxModel {
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


        static getPagePositionForComponent(component:JQuery):any {
            return $(component).position();
        }


        static getComponentInfo(component:JQuery):ComponentInfo {
            var t = this;
            return {
                type: t.getComponentType(component),
                key: t.getComponentKey(component),
                name: t.getComponentName(component),
                tagName: t.getTagNameForComponent(component)
            };
        }


        static getComponentType(component:JQuery):String {
            return component.data('live-edit-type');
        }


        static getComponentKey(component:JQuery):String {
            return component.data('live-edit-key');
        }


        static getComponentName(component:JQuery):String {
            return component.data('live-edit-name') || '[No Name]';
        }


        static getTagNameForComponent(component:JQuery):String {
            return component[0].tagName.toLowerCase();
        }


        static supportsTouch():Boolean {
            return document.hasOwnProperty('ontouchend');
        }

    }
}