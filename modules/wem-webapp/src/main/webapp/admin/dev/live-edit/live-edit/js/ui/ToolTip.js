AdminLiveEdit.ui.ToolTip = (function () {

    var OFFSET_X = 15;
    var OFFSET_Y = 15;


    function getToolTip() {
        return $liveedit('#live-edit-tool-tip');
    }


    function hide() {
        $liveedit('#live-edit-tool-tip').css({
            top: '-5000px',
            left: '-5000px'
        });
    }


    function createToolTip() {
        var $toolTip = $liveedit('<div id="live-edit-tool-tip" style="top:-5000px; left:-5000px;">' +
                                 '<img src="../live-edit/images/component_blue.png" style="padding-right: 7px; vertical-align: top"/>' +
                                 '<span id="live-edit-tool-tip-text"><!-- --></span>' +
                                 '</div>');
        $liveedit('body').append($toolTip);
    }


    function updateIcon(componentType) {
        getToolTip().find('img').attr('src', AdminLiveEdit.Util.getIconForComponent(componentType));
    }


    function updateText(text) {
        $liveedit('#live-edit-tool-tip-text').text(text);
    }


    function attachEventListeners() {
        $liveedit(document).on('mousemove', '[data-live-edit-type]', function (event) {
            if (AdminLiveEdit.ui.DragDrop.isDragging()) {
                hide();
                return;
            }
            var util = AdminLiveEdit.Util;
            var $component = $liveedit(event.target).closest('[data-live-edit-type]');
            var $toolTip = getToolTip();

            var pageX = event.pageX;
            var pageY = event.pageY;
            var viewPortSize = util.getViewPortSize();
            var scrollTop = util.getDocumentScrollTop();
            var toolTipWidth = $toolTip.width();
            var toolTipHeight = $toolTip.height();

            var xPos = pageX + OFFSET_X;
            var yPos = pageY + OFFSET_Y;

            if (xPos + toolTipWidth > (viewPortSize.width - OFFSET_X * 2)) {
                xPos = pageX - toolTipWidth - (OFFSET_X * 2);
            }

            if (yPos + toolTipHeight > (viewPortSize.height + scrollTop - OFFSET_Y * 2)) {
                yPos = pageY - toolTipHeight - (OFFSET_Y * 2);
            }

            $toolTip.css({
                top: yPos,
                left: xPos
            });

            var type = util.getTypeFromComponent($component);
            var name = util.getNameFromComponent($component);

            updateIcon(type);
            updateText(name);
        });

        $liveedit(document).on('mouseout', hide);
    }


    function registerSubscribers() {
        $liveedit.subscribe('/page/component/select', hide);
    }


    function init() {
        createToolTip();
        attachEventListeners();
        registerSubscribers();
    }


    // ***********************************************************************************************************************************//
    // Define public methods

    return {
        init: function () {
            init();
        }
    };

}());