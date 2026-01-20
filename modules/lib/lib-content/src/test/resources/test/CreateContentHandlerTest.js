var assert = require('/lib/xp/testing.js');
var content = require('/lib/xp/content.js');
var value = require('/lib/xp/value.js');

var expectedJson = {
    '_id': '123456',
    '_name': 'mycontent',
    '_path': '/a/b/mycontent',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'language': 'es',
    'valid': false,
    'data': {
        'a': 1,
        'b': 2,
        'c': [
            '1',
            '2'
        ],
        'd': {
            'e': {
                'f': 3.6,
                'g': true
            }
        },
        'times': {
            'time': '10:23:30',
            'date': '2016-12-06',
            'dateTime': '2016-12-06T15:54:30',
            'instant': '2016-12-06T15:54:30Z'
        }
    },
    'x': {
        'com-enonic-myapplication': {
            'myschema': {
                'a': 1
            }
        }
    },
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createContent = function () {
    var result = content.create({
        name: 'mycontent',
        parentPath: '/a/b',
        displayName: 'My Content',
        draft: true,
        contentType: 'test:myContentType',
        language: 'es',
        data: {
            a: 1,
            b: 2,
            c: ['1', '2'],
            d: {
                e: {
                    f: 3.6,
                    g: true
                }
            }, times: {
                time: value.localTime('10:23:30'),
                date: value.localDate('2016-12-06'),
                dateTime: value.localDateTime('2016-12-06T15:54:30'),
                instant: value.instant('2016-12-06T15:54:30Z')
            }
        },
        x: {
            'com-enonic-myapplication': {
                myschema: {
                    a: 1
                }
            }
        },
        'attachments': {},
    });

    assert.assertJsonEquals(expectedJson, result);
};

exports.createContentNameAlreadyExists = function () {
    try {
        content.create({
            name: 'mycontent',
            parentPath: '/a/b',
            displayName: 'My Content',
            contentType: 'test:myContentType',
            data: {}
        });

    } catch (e) {
        assert.assertEquals('contentAlreadyExists', e.getCode());
        return;
    }

    throw {message: 'Expected exception'};
};

var expectedJsonAutoGenerateName = {
    '_id': '123456',
    '_name': 'my-content',
    '_path': '/a/b/my-content',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createContentAutoGenerateName = function () {
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {}
    });

    assert.assertJsonEquals(expectedJsonAutoGenerateName, result);
};

var expectedJsonAutoGenerateName2 = {
    '_id': '123456',
    '_name': 'my-content-3',
    '_path': '/a/b/my-content-3',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createContentWithChildOrder = function () {
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        childOrder: 'field DESC',
        data: {}
    });

    assert.assertJsonEquals(expectedJsonWithChildOrder, result);
};

var expectedJsonWithChildOrder = {
    '_id': '123456',
    '_name': 'my-content',
    '_path': '/a/b/my-content',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'valid': false,
    'childOrder': 'field DESC',
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createContentAutoGenerateNameWithExistingName = function () {
    var counter = 1;
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {},
        idGenerator() {
            return String(counter++);
        }
    });

    assert.assertJsonEquals(expectedJsonAutoGenerateName2, result);
};

var expectedJsonWithWorkflow = {
    '_id': '123456',
    '_name': 'my-content',
    '_path': '/a/b/my-content',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'valid': false,
    'data': {},
    'x': {},
    'page': {},
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'PENDING_APPROVAL'
    }
};

exports.createContentWithWorkflow = function () {
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {},
        'workflow': {
            'state': 'PENDING_APPROVAL'
        }
    });

    assert.assertJsonEquals(expectedJsonWithWorkflow, result);
};

var expectedJsonWithPage = {
    '_id': '123456',
    '_name': 'my-content',
    '_path': '/a/b/my-content',
    'creator': 'user:system:anonymous',
    'createdTime': '1975-01-08T00:00:00Z',
    'type': 'test:myContentType',
    'displayName': 'My Content',
    'valid': false,
    'data': {},
    'x': {},
    'page': {
        'type': 'page',
        'path': '/',
        'descriptor': 'app:main-page',
        'config': {
            'greeting': 'Hello'
        },
        'regions': {
            'main': {
                'components': [
                    {
                        'path': '/main/0',
                        'type': 'part',
                        'descriptor': 'app:part-1',
                        'config': {
                            'enabled': true
                        }
                    },
                    {
                        'path': '/main/1',
                        'type': 'text',
                        'text': 'Some text content'
                    },
                    {
                        'path': '/main/2',
                        'type': 'layout',
                        'descriptor': 'app:layout-1',
                        'config': {
                            'layout': 'grid'
                        },
                        'regions': {
                            'section': {
                                'components': [
                                    {
                                        'path': '/main/2/section/0',
                                        'type': 'image',
                                        'image': 'image-id-001',
                                        'config': {
                                            'caption': 'An image caption'
                                        }
                                    },
                                    {
                                        'path': '/main/2/section/1',
                                        'type': 'fragment',
                                        'fragment': 'fragment-id-001'
                                    }
                                ],
                                'name': 'section'
                            }
                        }
                    }
                ],
                'name': 'main'
            }
        }
    },
    'attachments': {},
    'publish': {},
    'workflow': {
        'state': 'READY'
    }
};

exports.createContentWithPage = function () {
    var result = content.create({
        parentPath: '/a/b',
        displayName: 'My Content',
        contentType: 'test:myContentType',
        data: {},
        page: {
            type: 'page',
            path: '/',
            descriptor: 'app:main-page',
            config: {
                greeting: 'Hello'
            },
            regions: {
                main: {
                    components: [
                        {
                            type: 'part',
                            descriptor: 'app:part-1',
                            config: {
                                enabled: true
                            }
                        },
                        {
                            type: 'text',
                            text: 'Some text content'
                        },
                        {
                            type: 'layout',
                            descriptor: 'app:layout-1',
                            config: {
                                layout: 'grid'
                            },
                            regions: {
                                section: {
                                    components: [
                                        {
                                            type: 'image',
                                            image: 'image-id-001',
                                            config: {
                                                caption: 'An image caption'
                                            }
                                        },
                                        {
                                            type: 'fragment',
                                            fragment: 'fragment-id-001'
                                        }
                                    ]
                                }
                            }
                        }
                    ]
                }
            }
        }
    });

    assert.assertJsonEquals(expectedJsonWithPage, result);
};
