function editor(c) {
    c.displayName = 'Modified';
    c.data.a++;
    c.data.z = '99';

    c.metadata['mymodule:other'] = {
        name: 'test'
    };

    return c;
}

exports.modify_notFound = function () {
    var result = execute('content.modify', {
        key: '123456',
        editor: editor
    });

    assert.assertNull(result);
};

exports.modifyById = function () {
    var result = execute('content.modify', {
        key: '123456',
        editor: editor
    });

    assert.assertNull(result);
};

exports.modifyByPath = function () {
    var result = execute('content.modify', {
        key: '/a/b/mycontent',
        editor: editor
    });

    assert.assertNull(result);
};
