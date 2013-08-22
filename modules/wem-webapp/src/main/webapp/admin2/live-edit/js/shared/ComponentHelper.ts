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

interface HighlighterStyle {
    strokeColor: string;
    strokeDashArray: string;
    fillColor: string;
}

interface ComponentPagePosition {
    top: number;
    left: number;
}

module LiveEdit {
    export class ComponentHelper {

        static $:JQuery = $liveEdit;

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

        public static getHighlighterStyleForComponent(component:JQuery):HighlighterStyle {
            var componentType:string = ComponentHelper.getComponentType(component);

            var strokeColor,
                strokeDashArray,
                fillColor;

            switch (componentType) {
            case 'region':
                strokeColor = 'rgba(20,20,20,1)';
                strokeDashArray = '';
                fillColor = 'rgba(255,255,255,0)';
                break;

            case 'layout':
                strokeColor = 'rgba(255,165,0,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(100,12,36,0)';
                break;

            case 'part':
                strokeColor = 'rgba(68,68,68,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(255,255,255,0)';
                break;

            case 'image':
                strokeColor = 'rgba(68,68,68,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(255,255,255,0)';
                break;

            case 'paragraph':
                strokeColor = 'rgba(85,85,255,1)';
                strokeDashArray = '5 5';
                fillColor = 'rgba(255,255,255,0)';
                break;

            case 'content':
                strokeColor = '';
                strokeDashArray = '';
                fillColor = 'rgba(0,108,255,.25)';
                break;

            default:
                strokeColor = 'rgba(20,20,20,1)';
                strokeDashArray = '';
                fillColor = 'rgba(255,255,255,0)';
            }

            return {
                strokeColor: strokeColor,
                strokeDashArray: strokeDashArray,
                fillColor: fillColor
            }
        }

        /**
         * Will be shared with Context Window later in the project
         */
        public static resolveCssClassForComponent(component:JQuery):string {
            var iconCls:string;
            var componentType = ComponentHelper.getComponentType(component);

            switch (componentType) {
            case 'page':
                iconCls = 'live-edit-context-menu-page-icon';
                break;

            case 'region':
                iconCls = 'live-edit-context-menu-region-icon';
                break;

            case 'layout':
                iconCls = 'live-edit-context-menu-layout-icon';
                break;

            case 'part':
                iconCls = 'live-edit-context-menu-part-icon';
                break;

            case 'content':
                iconCls = 'live-edit-context-menu-content-icon';
                break;

            case 'paragraph':
                iconCls = 'live-edit-context-menu-paragraph-icon';
                break;

            default:
                iconCls = '';
            }

            return iconCls;
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