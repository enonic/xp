StartTest(function (t) {

    function getWindows() {
        return $liveedit('[data-live-edit-type=window]');
    }

    function getRegions() {
        return $liveedit('[data-live-edit-type=region]');
    }

    t.ok(AdminLiveEdit.Util, 'Class: AdminLiveEdit.Util exists');

    t.diag('Test: getBoxModel');

    var window5 = getWindows()[4];
    var boxModel = AdminLiveEdit.Util.getBoxModel(window5);
    t.is(boxModel.width, 130, 'Width is 300');
    t.is(boxModel.height, 100, 'Height is 200');
    t.is(boxModel.top, 136, 'Offset top is 136');
    t.is(boxModel.left, 201, 'Offset left is 201');
    t.is(boxModel.paddingBottom, 0, 'Padding bottom is 0');
    t.is(boxModel.borderTop, 0, 'Border top is 0');

    var window6 = getWindows()[5];
    boxModel = AdminLiveEdit.Util.getBoxModel(window6);

    t.is(boxModel.paddingTop, 10, 'Padding bottom is 10');
    t.is(boxModel.paddingRight, 10, 'Padding bottom is 10');
    t.is(boxModel.paddingBottom, 10, 'Padding bottom is 10');
    t.is(boxModel.paddingLeft, 10, 'Padding bottom is 10');

    boxModel = AdminLiveEdit.Util.getBoxModel(window6, true);
    t.is(boxModel.width, 110, 'Width is 110');
    t.is(boxModel.height, 80, 'Height is 80');

    var window7 = getWindows()[6];
    boxModel = AdminLiveEdit.Util.getBoxModel(window7);
    t.is(boxModel.borderTop, 1, 'Border top for window 7 is 1');
    t.is(boxModel.borderRight, 1, 'Border right for window 7 is 1');
    t.is(boxModel.borderBottom, 1, 'Border bottom for window 7 is 1');
    t.is(boxModel.borderLeft, 1, 'Border left for window 7 is 1');


    // ****

    t.diag('Test: getIconForComponent');

    t.is(AdminLiveEdit.Util.getIconForComponent('region'), '../app/images/layout_vertical.png', 'Region icon ok');
    t.is(AdminLiveEdit.Util.getIconForComponent('window'), '../app/images/component_blue.png', 'Window icon ok');
    t.is(AdminLiveEdit.Util.getIconForComponent('content'), '../app/images/data_blue.png', 'Content icon ok');
    t.is(AdminLiveEdit.Util.getIconForComponent('paragraph'), '../app/images/text_rich_marked.png', 'Paragraph icon ok');
    t.is(AdminLiveEdit.Util.getIconForComponent('page'), '../app/images/document_plain_blue.png', 'Page icon ok');
    t.is(AdminLiveEdit.Util.getIconForComponent('fisk'), '../app/images/component_blue.png', 'Unknown icon ok');


    // ****

    t.diag('Test: getPageComponentPagePosition');

    var pos = AdminLiveEdit.Util.getPageComponentPagePosition(getWindows()[4]);
    t.is(pos.top, 126, 'Top pos for window 5 is ok');
    t.is(pos.left, 201, 'Left pos for window 5 is ok');

    pos = AdminLiveEdit.Util.getPageComponentPagePosition(getWindows()[8]);
    t.is(pos.top, 236, 'Top pos for window 9 is ok');
    t.is(pos.left, 391, 'Left pos for window 9 is ok');


    // ****

    t.diag('Test: getPageComponentInfo');

    var info = AdminLiveEdit.Util.getComponentInfo($liveedit(getWindows()[4]));
    t.is(info.type, 'window', 'type is window');
    t.is(info.key, '7', 'key is 7');
    t.is(info.name, 'Window 5', 'name is Window 5');

    info = AdminLiveEdit.Util.getComponentInfo($liveedit(getRegions()[1]));
    t.is(info.type, 'region', 'type is region');
    t.is(info.key, '5', 'key is 5');
    t.is(info.name, 'Region 2', 'name is Region 2');


    // ***

    t.diag('Test: getComponentType');

    t.is(AdminLiveEdit.Util.getComponentType($liveedit(getWindows()[4])), 'window', 'type for Window 5 is "window"');
    t.is(AdminLiveEdit.Util.getComponentType($liveedit(getRegions()[1])), 'region', 'type for Region 2 is "region"');


    // ***

    t.diag('Test: getComponentKey');

    t.is(AdminLiveEdit.Util.getComponentKey($liveedit(getWindows()[4])), '7', 'key for Window 5 is 7');
    t.is(AdminLiveEdit.Util.getComponentKey($liveedit(getRegions()[1])), '5', 'key for Region 2 is 5');


    // ***

    t.diag('Test: getComponentName');

    t.is(AdminLiveEdit.Util.getComponentName($liveedit(getWindows()[4])), 'Window 5', 'name for Window 5 is "Window 5"');
    t.is(AdminLiveEdit.Util.getComponentName($liveedit(getRegions()[1])), 'Region 2', 'name for Region 2 is "Region 2"');


    // ***

    t.diag('Test: getTagNameForComponent');

    t.is(AdminLiveEdit.Util.getTagNameForComponent($liveedit(getWindows()[4])), 'div', 'tag name for Window 5 is "div"');
    t.is(AdminLiveEdit.Util.getTagNameForComponent($liveedit(getRegions()[1])), 'section', 'tag name for Region 2 is "section"');
    t.is(AdminLiveEdit.Util.getTagNameForComponent($liveedit(getRegions()[2])), 'aside', 'tag name for Region 2 is "aside"');


});