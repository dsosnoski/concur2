concur2
============

This gives sample Java and Scala code for the second article in my JVM Concurrency series on IBM
developerWorks. The project uses a Maven build with a dependency on the [code from the first article](https://github.com/dsosnoski/concur1),
so you'll need to do a `mvn clean install` on that project before you can build and run this one. Once
you've installed the first project, you can build this one with another `mvn clean install` to get
everything to a working state.

The code for this project is all in the `com.sosnoski.concur.article2` package
in the *main/java* trees. The *data* directory contains the same lists of known words and misspelled words as included
in the code for the first article.

To run timing tests of the edit distance code variations use
`mvn scala:run -Dlauncher={name} -DaddArgs={block size}`, where {name} selects the test code:

1. `completeable0` - `CompletableFuture` code with simple for loops (`CompletableFutureDistance0` class)
2. `completeable1` - `CompletableFuture` code with completion handler using `CountDownLatch` (`CompletableFutureDistance1` class)
3. `completeable0` - `CompletableFuture` code with composed futures (`CompletableFutureDistance2` class)
4. `compstream` - `CompletableFuture` code with stream (`CompletableFutureStreamDistance` class)
5. `chunkpar` - Chunked parallel computations (`ChunkedParallelDistance` class)
6. `nchunkpar` - Full stream (nonchunked) parallel computations (`NonchunkedParallelDistance` class)
7. `forkjoinstr` - Fork-join code with stream (`ForkJoinStreamDistance` class)
8. `forkjoin` - Java `ForkJoinPool` with recursive task splitting from first article (`ForkJoinDistance` class)
9. `parcol` - Scala using parallel collection from first article (`ParallelCollectionDistance` class)

and {block size} is the number of known words to include in each calculation task.

I had problems importing the project into Eclipse for Java 8 (Luna build), so I've included my
Eclipse project files in this repository. You'll need to modify them for your system if you do
an import, but at least it gives a starting point.

