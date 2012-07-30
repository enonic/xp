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


        getPageComponentPagePosition: function (element) {
            return $liveedit(element).position();
        },


        getPageComponentInfo: function ($component) {
            var t = this;
            return {
                type: t.getPageComponentType($component),
                key: t.getPageComponentKey($component),
                name: t.getPageComponentName($component)
            };
        },


        getPageComponentType: function ($component) {
            return $component[0].getAttribute('data-live-edit-type');
        },


        getPageComponentKey: function ($component) {
            return $component[0].getAttribute('data-live-edit-key');
        },


        getPageComponentName: function ($component) {
            return $component[0].getAttribute('data-live-edit-name');
        },


        supportsTouch: function () {
            return document.hasOwnProperty('ontouchend');
        }

    };

}());

