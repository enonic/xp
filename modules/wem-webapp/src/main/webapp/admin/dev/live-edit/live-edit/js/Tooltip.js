AdminLiveEdit.Tooltip = (function () {
    function getTooltip() {
        return $liveedit('#live-edit-tooltip');
    }


    function setText(typeText, nameText) {
        $liveedit('#live-edit-tooltip-type-text').html(typeText);
        $liveedit('#live-edit-tooltip-name-text').html(nameText);
    }


    function scrollTooltipIntoView() {
        var util = AdminLiveEdit.Util;
        var componentTopPosition = util.getPageComponentPagePosition(getTooltip()).top;
        if (componentTopPosition <= window.pageYOffset) {
            $liveedit('html, body').animate({scrollTop: componentTopPosition - 10}, 200);

        }
    }


    function moveToComponent(event, $component) {
        var util = AdminLiveEdit.Util;
        var componentType = util.getPageComponentType($component);
        var componentName = util.getPageComponentName($component);

        setText(componentType, ' - ' + componentName);

        var $tooltip = getTooltip();
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);
        var top = componentBoxModel.top - 50;
        var left = componentBoxModel.left + (componentBoxModel.width / 2) - ($tooltip.outerWidth() / 2);

        $tooltip.css({
            top: top,
            left: left
        });
        scrollTooltipIntoView();
    }


    function hide() {
        $liveedit('#live-edit-tooltip').css({
            top : '-5000px',
            left: '-5000px'
        });
    }


    function createTooltip() {
        var $tooltip = $liveedit('<div id="live-edit-tooltip" style="top:-5000px; left:-5000px;"><span id="live-edit-tooltip-type-text"><!-- --></span><span id="live-edit-tooltip-name-text"><!-- --></span><div id="live-edit-tooltip-arrow-border"></div><div id="live-edit-tooltip-arrow"></div></div>');
        $liveedit('body').append($tooltip);
    }


    function registerSubscribers() {
        $liveedit.subscribe('/page/component/select', moveToComponent);
        $liveedit.subscribe('/page/component/deselect', hide);
    }


    function init() {
        createTooltip();
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