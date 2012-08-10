AdminLiveEdit.ui.InfoTip = (function () {
    function getInfoTip() {
        return $liveedit('#live-edit-info-tip');
    }


    function updateText(text) {
        $liveedit('#live-edit-info-tip-name-text').html(text);
    }


    function updateIcon(componentType) {
        getInfoTip().find('img').attr('src', AdminLiveEdit.Util.getIconForComponent(componentType));
    }


    function moveToComponent(event, $component) {
        var util = AdminLiveEdit.Util;
        var componentName = util.getComponentName($component);
        var componentType = util.getComponentType($component);

        updateText(componentName);
        updateIcon(componentType);

        var $infoTip = getInfoTip();
        var componentBoxModel = AdminLiveEdit.Util.getBoxModel($component);
        var top = componentBoxModel.top - 50;
        var left = componentBoxModel.left + (componentBoxModel.width / 2) - ($infoTip.outerWidth() / 2);

        $infoTip.css({
            top: top + 12,
            left: left
        });
    }


    function hide() {
        $liveedit('#live-edit-info-tip').css({
            top: '-5000px',
            left: '-5000px'
        });
    }


    function createInfoTip() {
        var $infoTip = $liveedit('<div id="live-edit-info-tip" style="top:-5000px; left:-5000px;">' +
                                 '<img src="../resources/images/component_blue.png" style="padding-right: 7px; vertical-align: top"/>' + // TODO: Create a class
                                 '<span id="live-edit-info-tip-name-text"><!-- --></span>' +
                                 '<div id="live-edit-info-tip-arrow-border"></div><div id="live-edit-info-tip-arrow"></div></div>');
        $liveedit('body').append($infoTip);
    }


    function registerSubscribers() {
        $liveedit.subscribe('/ui/selectedcomponent/on-select', moveToComponent);
        $liveedit.subscribe('/ui/selectedcomponent/on-deselect', hide);
    }


    function init() {
        createInfoTip();
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