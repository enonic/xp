StartTest(function (t) {

    var $ = $liveedit;
    t.ok(AdminLiveEdit.view.Base, 'Class: AdminLiveEdit.view.Base');

    var base = new AdminLiveEdit.view.Base();
    var element = base.createElement('<div/>');

    t.ok(element.is('div') === true, 'Created element should be a HTML DIV element');
    t.is(element.attr('id'), 'live-edit-ui-cmp-0', '@id should be auto-generated and value should be "live-edit-cmp0"');
    t.ok($(base.getEl()).jquery && $(base.getEl()).length === 1, 'getEl() should return a jQuery object');


    element.appendTo($('body'));
    t.ok($('body').children('#live-edit-ui-cmp-0').length === 1, 'Element should be appended to the BODY element');


    base.getEl().attr('data-live-edit-type', 'region');
    base.setCssPosition(base.getEl());

    t.is(element.css('position'), 'absolute', 'Element should have CSS position absolute');

});