AdminLiveEdit.namespace.useNamespace('AdminLiveEdit.Util');

AdminLiveEdit.Util = (function ($) {
    'use strict';

    return {
        getDocumentSize: function () {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        },


        getViewPortSize: function () {
            var $window = $(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        },


        getDocumentScrollTop: function () {
            return $(document).scrollTop();
        },


        getBoxModel: function ($element, contentOnly) {
            var $el = $($element);
            var offset = $el.offset();
            var top = offset.top;
            var left = offset.left;
            var width = $el.outerWidth();
            var height = $el.outerHeight();

            var mt = parseInt($el.css('marginTop'), 10);
            var mr = parseInt($el.css('marginRight'), 10);
            var mb = parseInt($el.css('marginBottom'), 10);
            var ml = parseInt($el.css('marginLeft'), 10);

            var bt = parseInt($el.css('borderTopWidth'), 10);
            var br = parseInt($el.css('borderRightWidth'), 10);
            var bb = parseInt($el.css('borderBottomWidth'), 10);
            var bl = parseInt($el.css('borderLeftWidth'), 10);

            var pt = parseInt($el.css('paddingTop'), 10);
            var pr = parseInt($el.css('paddingRight'), 10);
            var pb = parseInt($el.css('paddingBottom'), 10);
            var pl = parseInt($el.css('paddingLeft'), 10);

            // TODO calculate margin and border
            if (contentOnly) {
                top = top + pt;
                left = left + pl;
                width = width - (pl + pr);
                height = height - (pt + pb);
            }

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
        },


        getIconForComponent: function (componentType) {
            var icon = '';
            switch (componentType) {
            case 'region':
                icon = '../../../admin2/live-edit/images/layout_vertical.png';
                break;
            case 'part':
                icon = '../../../admin2/live-edit/images/component_blue.png';
                break;
            case 'content':
                icon = '../../../admin2/live-edit/images/data_blue.png';
                break;
            case 'paragraph':
                icon = '../../../admin2/live-edit/images/text_rich_marked.png';
                break;
            default:
                // TODO: Use a fallback icon?
                icon = '../../../admin2/live-edit/images/component_blue.png';
            }
            return icon;
        },


        getPagePositionForComponent: function ($component) {
            return $($component).position();
        },


        getComponentInfo: function ($component) {
            var t = this;
            return {
                type: t.getComponentType($component),
                key: t.getComponentKey($component),
                name: t.getComponentName($component),
                tagName: t.getTagNameForComponent($component)
            };
        },


        getComponentType: function ($component) {
            return $component.data('live-edit-type');
        },


        getComponentKey: function ($component) {
            return $component.data('live-edit-key');
        },


        getComponentName: function ($component) {
            return $component.data('live-edit-name') || '[No Name]';
        },


        getTagNameForComponent: function ($component) {
            return $component[0].tagName.toLowerCase();
        },


        supportsTouch: function () {
            return document.hasOwnProperty('ontouchend');
        }

    };
}($liveedit));

