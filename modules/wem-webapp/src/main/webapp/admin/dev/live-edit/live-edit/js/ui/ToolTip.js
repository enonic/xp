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
            var util = AdminLiveEdit.Util;

            var $component = $liveedit(event.target).closest('[data-live-edit-type]');
            var $toolTip = getToolTip();
            var type = util.getTypeFromComponent($component);
            var name = util.getNameFromComponent($component);

            $toolTip.css({
                top: event.pageY + OFFSET_Y,
                left: event.pageX + OFFSET_X
            });

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