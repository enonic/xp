exports.deleteById = function () {
    var result = execute('content.delete', {
        key: '123456'
    });

    assert.assertEquals(true, result);
};

exports.deleteByPath = function () {
    var result = execute('content.delete', {
        key: '/a/b'
    });

    assert.assertEquals(true, result);
};

exports.deleteById_notFound = function () {
    var result = execute('content.delete', {
        key: '123456'
    });

    assert.assertEquals(false, result);
};

exports.deleteByPath_notFound = function () {
    var result = execute('content.delete', {
        key: '/a/b'
    });

    assert.assertEquals(false, result);
};
