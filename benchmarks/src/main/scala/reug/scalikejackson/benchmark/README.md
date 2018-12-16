## Library benchmarks results
```
[info] Benchmark                         Mode  Cnt  Score   Error   Units
[info] AlterBenchmark.mutateBigLite  thrpt       0.213          ops/ms
[info] AlterBenchmark.mutateBigPlay  thrpt       0.161          ops/ms
```

```
[info] Benchmark                         Mode  Cnt   Score   Error   Units
[info] ReadBenchmark.unmarshalLite      thrpt        9.335          ops/ms
[info] ReadBenchmark.unmarshalLiteJson  thrpt       13.234          ops/ms
[info] ReadBenchmark.unmarshalPlay      thrpt        1.768          ops/ms
[info] ReadBenchmark.unmarshalPlayJson  thrpt        8.455          ops/ms
[info] ReadBenchmark.unmarshalSeqLite   thrpt        3.290          ops/ms
[info] ReadBenchmark.unmarshalSeqPlay   thrpt        0.945          ops/ms
```

```
[info] Benchmark                             Mode  Cnt  Score   Error   Units
[info] SearchBenchmark.searchBigLinearLite  thrpt       0.353          ops/ms
[info] SearchBenchmark.searchBigLinearPlay  thrpt       0.091          ops/ms
[info] SearchBenchmark.searchDeepLite       thrpt       4.868          ops/ms
[info] SearchBenchmark.searchDeepPlay       thrpt       1.173          ops/ms
[info] SearchBenchmark.searchLinearLite     thrpt       5.520          ops/ms
[info] SearchBenchmark.searchLinearPlay     thrpt       2.389          ops/ms
```

```
[info] Benchmark                               Mode  Cnt   Score   Error   Units
[info] JacksonLiteBenchmarkWrite.marshalLite  thrpt       14.409          ops/ms
[info] JacksonLiteBenchmarkWrite.marshalPlay  thrpt        3.236          ops/ms
```