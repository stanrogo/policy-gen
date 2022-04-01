# policy-gen

Transform a csv of boolean policies into XACML, IBM and Oracle formats.

To compile: `mvn clean package`

To run: `java -jar target/policy-gen-1.0.0-jar-with-dependencies.jar` with the following options:

* `xacml -i=<input> -d=<outputDir>`
* `db2 -i=<input> -o=<outputFile>`
* `oracle -i=<input> -o=<outputFile> -s=<schema>`

A sample input `policies.csv` is provided.
