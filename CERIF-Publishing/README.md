# CERIF Publishing

The CERIF Publishing tool is used to produce documentation about:
* the CERIF data model
* the semantic vocabularies

The documentation produced by this tool is published on the euroCRIS website.

## Objective

The objective of this tool it to produce:
* an rdf description of the CERIF data model
* an HTML human readable version of each XML CERIF semantic vocabulary
* an RDF description of each XML CERIF semantic vocabulary
* a ready-to-deploy structure to be published on the euroCRIS website

## Building documentation

The documentation is built using [Apache Maven](https://maven.apache.org/), which should be installed on the computer that is used to produce the documentation.

A [Project Object Model](pom.xml) has been defined to automatically build the documentation following these steps:
1. retrieve the content of the [CERIF data model source](#user-content-the-cerif-data-model)
2. retrieve the content of the [CERIF semantic vocabularies source](#user-content-the-semantic-vocabularies)
3. build the CSS files issued from the [stylesheet sources](#user-content-stylesheet-for-human-readable-files)
4. transform the XML data model to RDF using the corresponding XSLT stylesheet
5. transform each XML vocabulary to RDF using the corresponding XSLT stylesheet
6. transform each XML vocabulary to HTML using the corresponding XSLT stylesheet
7. copy the original XML vocabularies to the result

To build the documentation: 
1. open a console on the computer
2. navigate to the local repository
3. run the following command:
```console 
mvn clean compile
```

## Sources

### The CERIF data model

By default, the data model used is the one hosted in the github project [EuroCRIS/CERIF-DataModel](https://github.com/EuroCRIS/CERIF-DataModel.git).

Any change to the data model must first be done in this repository before the documentation can be built using the CERIF Publishing tool.

The default source can be changed in the [Project Object Model](pom.xml) using the `git.datamodel.repo` property; a specific branch can also be targeted using the `git.datamodel.branch` property.

### The semantic vocabularies

By default, the semantic vocabularies used are the ones hosted in the github project [EuroCRIS/CERIF-Vocabularies](https://github.com/EuroCRIS/CERIF-Vocabularies.git).

As for the data model, any change to the vocabularies must first be done in this repository before the documentation can be built using the CERIF Publishing tool.

The default source can be changed in the [Project Object Model](pom.xml) using the `git.vocab.repo` property; a specific branch can also be targeted using the `git.vocab.branch` property.

The CERIF Publishing tool hosts [the schema](src/main/xsd) used to validate the XML semantic vocabularies.

### Stylesheet for human readable files

The CERIF Publishing tool contains [the source files](src/main/css) to build the CSS stylesheet used by the vocabularies when displayed in HTML format.

The source uses the [Bulma CSS framework](https://bulma.io/) and is written using the [SCSS syntax](https://sass-lang.com/documentation/syntax).

### XSLTransformation stylesheets

Many sources are expressed using XML.

The CERIF Publishing tool hosts the [XSLTransformation stylesheets](src/main/xslt) used to produce files from these sources.
