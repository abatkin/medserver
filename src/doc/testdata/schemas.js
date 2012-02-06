{
	"schemaName" : "testSchema",
	"fields" : [
		{
			"dataType" : "string",
			"name" : "firstName"
		},
		{
			"dataType" : "string",
			"name" : "lastName"
		},
		{
			"dataType" : "integer",
			"name" : "age"
		},
		{
			"dataType" : "boolean",
			"name" : "isAwesome"
		},
		{
			"dataType" : "selectFromOptions",
			"name" : "coverage",
			"optionValues" : [
				{
					"value" : NumberInt(0),
					"displayText" : "First Option"
				},
				{
					"value" : NumberInt(1),
					"displayText" : "Second Option"
				}
			]
		}
	],
	"active" : true
}