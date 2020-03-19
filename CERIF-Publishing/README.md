# CERIF Publishing

The CERIF Publishing tool is used to produce documentation about:
* the CERIF data model
* the semantic vocabularies

The documentation produced by this tool is published on the euroCRIS website.

## Objective

The objective of this tool it to produce:
* an RDF description of the CERIF data model
* an HTML human readable version of each XML CERIF semantic vocabulary
* an RDF description of each XML CERIF semantic vocabulary
* a ready-to-deploy structure to be published on the euroCRIS website

## Procedure

### Set up

1. Install [Apache Maven](https://maven.apache.org/).
2. Check out the following three projects in your workspace: [CERIF-DataModel](https://github.com/EuroCRIS/CERIF-DataModel), [CERIF-Vocabularies](https://github.com/EuroCRIS/CERIF-Vocabularies) and [CERIF-TG-Tools](https://github.com/EuroCRIS/CERIF-TG-Tools) (this project). Get the right branch combination.
3. Open a console on your computer.
4. Navigate to the directory with this README.md file:
```console
cd ~/workspace/CERIF-TG-Tools/CERIF-Publishing
```

### Validate the vocabularies

The XML semantic vocabularies are validated using [the XML Schema](src/main/xsd/CERIF-Vocabulary.xsd).

To run the validation task only:
1. Get [set-up](#set-up)
2. run the following command:
```console 
mvn clean process-sources
```

### Build the documentation

To build the documentation: 
1. Get [set-up](#set-up)
2. run the following command:
```console 
mvn clean compile
```

This:
- validates the CERIF vocabularies
- transforms the CERIF data model into RDF, storing the result as ``target/CERIF.rdf``
- transforms the CERIF vocabularies into RDF, storing the result into the ``target/vocab/rdf`` directory 
- transforms the CERIF vocabularies into HTML, storing the result into ``target/vocab/html`` directory
- copies the CERIF vocabularies in XML into the ``target/vocab/xml`` directory


## The Contents of this Sub-Project

### Stylesheet for human readable files (HTML)

The CERIF Publishing tool contains [the source files](src/main/css) to build the CSS stylesheet used by the vocabularies when displayed in HTML format.

The source uses the [Bulma CSS framework](https://bulma.io/) and is written using the [SCSS syntax](https://sass-lang.com/documentation/syntax).

### XSLTransformation stylesheets

The source CERIF artifacts are expressed in XML.

The CERIF Publishing tool hosts the [XSLTransformation stylesheets](src/main/xslt) used to produce files from these sources.
