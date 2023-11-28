## Build Requirements
- Maven
- Java 17
- jai-core-1.1.3.jar (https://repository.jboss.org/maven2/javax/media/jai-core/1.1.3/)

## Build Instructions
1. The Java Advanced Imaging API is not available in the Maven Central Repository. To install it, run the following command in the directory where you downloaded the JAR file:
    ```
    mvn install:install-file -Dfile="jai-core-1.1.3.jar" -DgroupId="javax.media" -DartifactId="jai_core" -Dversion="1.1.3" -Dpackaging="jar"
    ```
2. Clone this repository and navigate to the root directory.
3. Run `mvn clean compile package` to build the project.
4. Run `java -jar target/cs308-usms-1.0.0.jar` to run the program.