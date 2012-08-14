StartTest(function (t) {

    t.ok(AdminLiveEdit.HtmlElementReplacer, 'Class: AdminLiveEdit.HtmlElementReplacer exists');

    // ***

    t.diag('Instantiate HtmlElementReplacer');
    var replacer = new AdminLiveEdit.HtmlElementReplacer();

    var $flashObject = $liveedit('object');
    var $iFrame = $liveedit('iframe');
    t.is($flashObject.css('display'), 'none', 'Hide the (&lt;object&gt;) element');
    t.is($iFrame.css('display'), 'none', 'Hide the (&lt;iframe&gt;) element');


    var $placeholders = $liveedit('.live-edit-html-element-placeholder');
    t.is($placeholders.length, 3, 'Create placeholders');


    var flashPlaceholder = $placeholders[0];
    var iFramePlaceholder = $placeholders[1];
    var appletPlaceholder = $placeholders[2];
    t.is(flashPlaceholder.style.width, '100%', 'The flash placeholder width is 100%');
    t.is(flashPlaceholder.style.height, '240px', 'The flash placeholder height is 240px');
    t.is(iFramePlaceholder.style.width, '280px', 'The iframe placeholder width is 100%');
    t.is(iFramePlaceholder.style.height, '350px', 'The iframe placeholder height is 240px');
    t.is(appletPlaceholder.style.width, '400px', 'The applet placeholder width is 400px');
    t.is(appletPlaceholder.style.height, '80px', 'The applet placeholder height is 80px');


    var $flashObjectIcon = $liveedit(flashPlaceholder).find('div');
    t.is($flashObjectIcon.hasClass('live-edit-object'), true, 'The flash placeholder icon is \'object\'');

    var $iFrameIcon = $liveedit(iFramePlaceholder).find('div');
    t.is($iFrameIcon.hasClass('live-edit-iframe'), true, 'The iframe placeholder icon is \'iframe\'');

    var $appletIcon = $liveedit(appletPlaceholder).find('div');
    t.is($appletIcon.hasClass('live-edit-object'), true, 'The applet placeholder icon is \'object\'');

});