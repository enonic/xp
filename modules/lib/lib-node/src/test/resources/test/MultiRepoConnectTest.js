var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/testing');

exports.query = function () {
    var searchConnection = nodeLib.multiRepoConnect({
        sources: [
            {
                repoId: 'my-repo',
                branch: 'my-branch',
                principals: ['role:system.admin']
            },
            {
                repoId: 'my-other-repo',
                branch: 'master',
                principals: ['role:system.admin']
            }
        ]
    });

    var result = searchConnection.query({
        start: 0,
        count: 2,
        query: 'startTime > instant(\'2016-10-11T14:38:54.454Z\')'
    });

    var expected = {
        'total': 12902,
        'count': 2,
        'hits': [
            {
                'id': 'b186d24f-ac38-42ca-a6db-1c1bda6c6c26',
                'score': 1.2300000190734863,
                'repoId': 'my-repo',
                'branch': 'master'
            },
            {
                'id': '350ba4a6-589c-498b-8af0-f183850e1120',
                'score': 1.399999976158142,
                'repoId': 'com.enonic.cms.default',
                'branch': 'draft'
            }
        ]
    };

    assert.assertJsonEquals(expected, result);
};
