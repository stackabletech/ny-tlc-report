# NY TLC Report

Build a simple report from the New York travel dataset.

# Build

Use Java 17 (e.g. `nix-shell -p jdk17`)!

mvn package -P spark-3.5.7

# Run on Kubernetes

See examples in the Stackable operator [https://github.com/stackabletech/spark-k8s-operator/blob/main/examples/ny-tlc-report-pvc.yaml](https://github.com/stackabletech/spark-k8s-operator/blob/main/examples/ny-tlc-report-pvc.yaml)
