# set JAVA_HOME for Maven
export JAVA_HOME=/usr/lib/jvm/`rpm -q java-11-openjdk-devel | sed s/devel-//g`
# Build the application
mvn package
