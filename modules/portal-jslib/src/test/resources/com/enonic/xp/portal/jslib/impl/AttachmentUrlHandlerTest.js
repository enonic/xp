exports.createUrl_with_name = function () {
    var result = execute('portal.attachmentUrl', {
        name: "myattachment.pdf",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/stage/some/path/_/attachment/myattachment.pdf?a=1&b=1&b=2', result);
};

exports.createUrl_with_label = function () {
    var result = execute('portal.attachmentUrl', {
        label: "source",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/stage/some/path/_/attachment/source?a=1&b=1&b=2', result);
};


exports.createUrl_with_id_and_name = function () {
    var result = execute('portal.attachmentUrl', {
        id: "123",
        name: "myattachment.pdf",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/stage/some/path/_/attachment/id/123/myattachment.pdf?a=1&b=1&b=2', result);
};

exports.createUrl_with_id_and_label = function () {
    var result = execute('portal.attachmentUrl', {
        id: "123",
        label: "source",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    assert.assertEquals('/portal/stage/some/path/_/attachment/id/123/source?a=1&b=1&b=2', result);
};
