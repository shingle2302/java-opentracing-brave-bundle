# OpenTracing Brave Tracer Bundle

OpenTracing [Brave](https://github.com/openzipkin-contrib/brave-opentracing) Bundle. It builds on top of the [brave](https://github.com/apache/incubator-zipkin-brave/tree/master/brave) artifact.

This project builds a fat-jar with all its dependencies, along with simple configuration, in order to ease deployment, which can then be retrieved using the `TracerFactory` pattern (from [java-tracerresolver](https://github.com/opentracing-contrib/java-tracerresolver) ). It can be used with any OpenTracing instrumentation library.

```sh
java -cp:$MYCLASSPATH:brave-bundle-0.0.1-SNAPSHOT.jar com.mycompany.MyService
```

## Development

```sh
mvn package
```

## License

[Apache 2.0 License](./LICENSE).
