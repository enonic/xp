AdminLiveEdit.Util = (function () {
    return {
        getDocumentSize: function () {
            var $document = $liveedit(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        },


        getViewPortSize: function () {
            var $window = $liveedit(window);
            return {
                width: $window.width(),
                height: $window.height()
            };
        },


        getDocumentScrollTop: function() {
            return $liveedit(document).scrollTop();
        },


        getBoxModel: function ($element, contentOnly) {
            var $el = $liveedit($element);
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
                icon = '../live-edit/images/layout_vertical.png';
                break;
            case 'window':
                icon = '../live-edit/images/component_blue.png';
                break;
            case 'content':
                icon = '../live-edit/images/data_blue.png';
                break;
            case 'paragraph':
                icon = '../live-edit/images/text_rich_marked.png';
                break;
            case 'page':
                icon = '../live-edit/images/document_plain_blue.png';
                break;
            default:
                icon = '';
            }
            return icon;
        },


        getPageComponentPagePosition: function ($element) {
            return $liveedit($element).position();
        },


        getPageComponentInfo: function ($component) {
            var t = this;
            return {
                type: t.getTypeFromComponent($component),
                key: t.getKeyFromComponent($component),
                name: t.getNameFromComponent($component)
            };
        },


        getTypeFromComponent: function ($component) {
            return $component[0].getAttribute('data-live-edit-type');
        },


        getKeyFromComponent: function ($component) {
            return $component[0].getAttribute('data-live-edit-key');
        },


        getNameFromComponent: function ($component) {
            return $component[0].getAttribute('data-live-edit-name');
        },


        getTagNameForComponent: function ($component) {
            return $component[0].tagName.toLowerCase();
        },


        supportsTouch: function () {
            return document.hasOwnProperty('ontouchend');
        },


        createGUID: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        }


    };

}());

