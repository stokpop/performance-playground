# Stokpop's Performance Playground

## Byteman tracing

Generate byteman trace file via `nl.stokpop.byteman.RuleGeneratorTraceCallDuration`.

Run SpringBoot app: `nl.stokpop.server.StokpopServer`

```text
export BYTEMAN_HOME=/path/to/byteman-download-4.0.13
$BYTEMAN_HOME/bin/bminstall.sh <pid>
$BYTEMAN_HOME/bin/bmsubmit.sh byteman/pdf-service.btm
```

Next call create pdf:

```text
curl -Ss -o hello-world.pdf  localhost:8080/download-stream
```

In output you can see:

```text
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 1547308
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 342150
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 288575
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 300371
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 261888
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 242383
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 239091
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 501809
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 345748
[BYTEMAN] [task-2] MakePdfService.addHelloWorldPage() elapsedTimeNanos = 245235
[BYTEMAN] [task-2] MakePdfService.createHelloWorldPdf() elapsedTimeNanos = 8440402
```