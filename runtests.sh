mvn scala:run -q -Dlauncher=forkjoin -DaddArgs=$1
mvn scala:run -q -Dlauncher=parcol -DaddArgs=$1
mvn scala:run -q -Dlauncher=completeable0 -DaddArgs=$1
mvn scala:run -q -Dlauncher=completeable1 -DaddArgs=$1
#mvn scala:run -q -Dlauncher=completeable2 -DaddArgs=$1
mvn scala:run -q -Dlauncher=compstream -DaddArgs=$1
mvn scala:run -q -Dlauncher=chunkpar -DaddArgs=$1
mvn scala:run -q -Dlauncher=nchunkpar -DaddArgs=$1
mvn scala:run -q -Dlauncher=forkjoinstr -DaddArgs=$1

