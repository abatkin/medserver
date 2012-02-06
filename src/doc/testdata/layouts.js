{
        "formName" : "testForm",
        "schemaName" : "testSchema",
        "description" : "This is a test form",
        "title" : "Test Form",
        "links" : [
                {
                	name: "about",
                	displayText: "About",
                	linkType: "DataLink",
                	displayData: "This is a big form for you to fill out. We like forms. Please fill out the whole thing."
                },
                {
                	name: "github",
                	displayText: "GitHub",
                	linkType: "ExternalLink",
                	destinationUrl: "https://github.com/abatkin/medserver"
                }
        ],
        "sections" : [
                {
                        "title" : "Section One",
                        "instructions" : "This is section one. Please fill out all of the information",
                        "fields" : [
                                {
                                		"widgetType" : "string",
                                        "name" : "firstName",
                                        "title" : "First Name",
                                        "helpText" : "Please enter your first name"
                                },
                                {
                                		"widgetType" : "string",
                                        "name" : "lastName",
                                        "title" : "Last Name",
                                        "helpText" : "Please enter your last name"
                                },
                                {
                                		"widgetType" : "boolean",
                                		"name" : "isAwesome",
                                		"title" : "Are you awesome",
                                		"helpText" : "Check here if you are awesome"
                                }
                        ]
                }
        ]
}
