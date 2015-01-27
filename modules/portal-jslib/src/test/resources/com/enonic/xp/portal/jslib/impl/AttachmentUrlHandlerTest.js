exports.createUrl = function () {
    var result = execute('portal.attachmentUrl', {
        name: "myattachment.pdf",
        params: {
            a: 1,
            b: [1, 2]
        }
    });

    // NOTE: This is not the actual url. Only a mock representation.
    assert.assertEquals('AttachmentUrlParams{params={a=[1], b=[1, 2]}, name=myattachment.pdf, download=false}', result);
};
