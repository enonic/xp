var contentLib = require('/lib/xp/content');

contentLib.query({
    start: 0,
    count: 2,
    filters: [
        {
            boolean: {
                must: [
                    {
                        exists: {
                            field: "modifiedTime"
                        }
                    },
                    {
                        exists: {
                            field: "another"
                        }
                    }
                ],
                mustNot: {
                    hasValue: {
                        field: "myField",
                        values: [
                            "cheese",
                            "fish",
                            "onion"
                        ]
                    }
                }
            },
            notExists: {
                field: "unwantedField"
            },
            ids: {
                values: ["id1", "id2"]
            }
        }
    ]
});