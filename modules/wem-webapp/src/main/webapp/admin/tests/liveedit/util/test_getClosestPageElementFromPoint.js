StartTest(function (t) {

    var link = $liveedit('#link');
    var pos = AdminLiveEdit.Util.getElementPagePosition(link);
    var pageElement = AdminLiveEdit.Util.getClosestPageElementFromPoint(pos.left, pos.top);

    t.diag('Get closest page element from link, position: (' + pos.left + ', ' + pos.top + ')');
    t.is(pageElement.attr('data-live-edit-name'), 'Window 2', 'Closest page element should be Window 2');

    var notAWindow = $liveedit('#not-a-window');
    pos = AdminLiveEdit.Util.getElementPagePosition(notAWindow);
    pageElement = AdminLiveEdit.Util.getClosestPageElementFromPoint(pos.left, pos.top);

    t.diag('Get closest page element from "not a window", position: (' + pos.left + ', ' + pos.top + ')');
    t.is(pageElement.attr('data-live-edit-name'), 'Region 3', 'Closest page element should be Region 3');


});