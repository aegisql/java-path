# java-path
## simple library for deep access to Java object hierarchy
### Maven dependencies
conveyor-core
```xml
<dependency>
  <groupId>com.aegisql</groupId>
  <artifactId>java-path</artifactId>
  <version>0.1.0</version>
</dependency>
```

To generate the parser code
```shell script
rm -rf src/main/javacc/com/* 
mvn clean generate-sources 
mv target/generated-sources/jjtree/com/aegisql/java_path/parser/* src/main/javacc/com/aegisql/java_path/parser/
```
