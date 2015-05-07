exports.createUrl = function () {
    var result = execute('portal.processHtml', {
        value: '<p><a title="Link tooltip" href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>'
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('ProcessHtmlParams{params={}, value=<p><a title="Link tooltip" ' +
                        'href="content://3e266eea-9875-4cb7-b259-41ad152f8532" target="_blank">link</a></p>}',
        result);

};
