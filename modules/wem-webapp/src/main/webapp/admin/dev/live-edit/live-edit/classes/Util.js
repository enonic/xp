AdminLiveEdit.Util = (function () {
    return {
        getDocumentSize : function () {
            var doc = $liveedit(document);
            return {
                width : doc.width(),
                height : doc.height()
            };
        },


        getViewPortSize : function () {
            var win = $liveedit(window);
            return {
                width : win.width(),
                height : win.height()
            };
        },


        getBoxModel : function (element, contentOnly) {
            var el = $liveedit(element);
            var offset = el.offset();
            var top = offset.top;
            var left = offset.left;
            var width = el.outerWidth();
            var height = el.outerHeight();

            var mt = parseInt(el.css('marginTop'), 10);
            var mr = parseInt(el.css('marginRight'), 10);
            var mb = parseInt(el.css('marginBottom'), 10);
            var ml = parseInt(el.css('marginLeft'), 10);

            var bt = parseInt(el.css('borderTopWidth'), 10);
            var br = parseInt(el.css('borderRightWidth'), 10);
            var bb = parseInt(el.css('borderBottomWidth'), 10);
            var bl = parseInt(el.css('borderLeftWidth'), 10);

            var pt = parseInt(el.css('paddingTop'), 10);
            var pr = parseInt(el.css('paddingRight'), 10);
            var pb = parseInt(el.css('paddingBottom'), 10);
            var pl = parseInt(el.css('paddingLeft'), 10);

            // TODO calculate margin and border
            if (contentOnly) {
                top = top + pt;
                left = left + pl;
                width = width - (pl + pr);
                height = height - (pt + pb);
            }

            return {
                top : top,
                left : left,
                width : width,
                height : height,
                borderTop : bt,
                borderRight : br,
                borderBottom : bb,
                borderLeft : bl,
                paddingTop : pt,
                paddingRight : pr,
                paddingBottom : pb,
                paddingLeft : pl
            };
        },


        getElementPagePosition : function (element) {
            return $liveedit(element).position();
        },


        getClosestPageElementFromPoint : function (x, y) {
            var element = [];
            var elementFromPoint = $liveedit(this.elementFromPoint(x, y));

            var parent = elementFromPoint.parents('[data-live-edit-type=window]');
            var isWindowOrRegion = elementFromPoint.is('[data-live-edit-type=window]') ||
                                   elementFromPoint.is('[data-live-edit-type=region]');
            if (isWindowOrRegion) {
                element = elementFromPoint;
            } else if (parent.length > 0) {
                element = parent;
            } else {
                element = elementFromPoint.parents('[data-live-edit-type=region]');
            }
            return element;
        },


        getParentPageElement : function (element) {
            // Right now region is the only parent :)
            return element.parents('[data-live-edit-type=region]');
        },


        elementFromPoint : function (x, y) {
            return document.elementFromPoint(x, y);
        },


        getPageElementType : function (element) {
            return element.data().liveEditType;
        },


        getPageElementName : function (element) {
            return element.data().liveEditName;
        },


        isElementEmpty : function (element) {
            return $liveedit(element).children().length === 0;
        },


        supportsTouch : function () {
            return document.hasOwnProperty('ontouchend');
        },


        // ********************************************************************************************************** //
        // TODO: Everything below is even more prototyping.
        // Move this when things are clearer.

        /*
         windowCount: 0,
         insertWindowComponent: function(to)
         {
         this.windowCount++;

         var html = $liveedit( '<div data-live-edit-type="window" data-live-edit-name="Dummy window '+this.windowCount+'" style="margin-bottom: 10px"><h2>Window '+this.windowCount+'</h2>Far far away, behind the word mountains, far from the countries Vokalia and Consonantia, there live the blind texts. Separated they live in Bookmarksgrove right at the coast of the Semantics, a large language ocean. A small river named Duden flows by their place and supplies it with the necessary regelialia. It is a paradisematic country, in which roasted parts of sentences fly into your mouth. </div>' );
         $liveedit( to ).prepend( html ).trigger('liveedit.regionChange');
         },
         */


        getPageConfiguration : function () {
            function createWindowsArray(region) {
                var windows = [];
                region.find('[data-live-edit-type="window"]').each(function (i) {
                    windows.push({
                        "key" : i,
                        "name" : $liveedit(this).attr('data-live-edit-name')
                    });
                });

                return windows;
            }

            function createRegionsArray() {
                var regions = [];
                $liveedit('[data-live-edit-type="region"]').each(function (i) {
                    var region = $liveedit(this);
                    regions.push({
                        "key" : i,
                        "name" : region.attr('data-live-edit-name'),
                        "windows" : createWindowsArray(region)
                    });

                });
                return regions;
            }

            return {
                page : {
                    "name" : "Home",
                    "key" : 1987,
                    "regions" : createRegionsArray()
                }
            };
        }

    };

}());

