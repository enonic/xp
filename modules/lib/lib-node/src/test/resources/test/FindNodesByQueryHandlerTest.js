var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

var repo = nodeLib.connect({
    repoId: 'com.enonic.cms.default',
    branch: 'master'
});

exports.invalid = function () {

    try {
        repo.query({
            'start': 0,
            'count': 100,
            'query':
                [{
                    boolean: {
                        must: [{
                            term: {
                                name: 'type',
                                value: 'article'
                            }

                        }, {
                            fulltext: {
                                field: 'displayName',
                                query: 'fisk',
                                operator: 'AND'
                            }
                        }]
                    }
                }, {
                    boolean: {
                        must: {
                            term: {
                                name: 'type',
                                value: 'non-article'
                            }

                        }
                    }
                }]
        });
    } catch (e) {
        assert.assertEquals('query must be a String or JSON object', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};

var expectedEmptyJson = {
    'total': 0,
    'count': 0,
    'hits': []
};

var expectedSortJson = {
    'total': 12902,
    'count': 2,
    'hits': [
        {
            'id': 'b186d24f-ac38-42ca-a6db-1c1bda6c6c26',
            'score': 1.2300000190734863
        },
        {
            'id': '350ba4a6-589c-498b-8af0-f183850e1120',
            'score': 1.7000000476837158
        }
    ]
};

exports.queryEmpty = function () {
    var result = repo.query({
        'start': 0,
        'count': 100,
        'query': {}
    });
    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.queryNull = function () {
    var result = repo.query({
        'start': 0,
        'count': 100
    });
    assert.assertJsonEquals(expectedEmptyJson, result);
};

exports.sort = function () {
    var result = repo.query({
        'start': 0,
        'count': 100,
        'query': {},
        'sort': [
            {
                'field': '_modifiedTime',
                'direction': 'DESC'
            },
            {
                'type': 'geodistance',
                'field': 'myGeoPoint',
                'location': {
                    'lat': '2.2',
                    'lon': '3.3'
                }
            }
        ]
    });
    assert.assertJsonEquals(expectedSortJson, result);
};

exports.sortInvalid = function () {
    try {
        repo.query({
            'start': 0,
            'count': 100,
            'sort':
                function () {
                }
            ,
            'query': 'type = \'article\' AND fulltext(\'myField\', \'searching for cheese\', \'AND\') ',


        });
    } catch (e) {
        assert.assertEquals('sort must be a String, JSON object or array of JSON objects', e.getMessage());
        return;
    }

    throw {message: 'Expected exception'};
};

