# Unsigned primitives performance

Times in nanoseconds, DIFF in how much UInt is slower than Int

|              |                       | jvm (jdk 8)       | jvm (jdk 21)     | wasmWasi         | wasmJs Node      | macosArm64 Debug | macosArm64 Release | iosSimulatorArm64 Debug | iosSimulatorArm64 Release |
|--------------|-----------------------|-------------------|------------------|------------------|------------------|------------------|--------------------|-------------------------|---------------------------|
| long string  | INT  (ns)             | 12985100          | 13212008         | 13469158         | 13268854         | 107525491        | 12978074           | 107545266               | 13050404                  |
|              | UINT (ns)             | 12914504          | 12947842         | 37261433         | 36769799         | 861998262        | 22042316           | 863137295               | 21535353                  |
|              | DIFF (x times slower) | 0.994563307175147 | 0.98000561307562 | 2.76642630519295 | 2.77113600013988 | 8.0166875220314  | 1.69842736295077   | 8.02580464118244        | 1.65016753504336          |
|              |                       |                   |                  |                  |                  |                  |                    |                         |                           |
| short string | INT  (ns)             | 2845              | 1454             | 520              | 737              | 1454             | 250                | 1612                    | 262                       |
|              | UINT (ns)             | 3266              | 1583             | 558              | 1708             | 3041             | 275                | 3020                    | 312                       |
|              | DIFF (x times slower) | 1.14797891036907  | 1.08872077028886 | 1.07307692307692 | 2.31750339213026 | 2.09147180192572 | 1.1                | 1.87344913151365        | 1.19083969465649          |
