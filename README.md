# NY TLC Report

Build a simple report from the New York travel dataset.

# Build

mvn package

# Run on Docker

Create a sample input file:

    echo s3a://nyc-tlc/trip data/yellow_tripdata_2021-07.csv > input.txt

Submit the job:

    docker run --rm -it -v $(pwd):/repo docker.stackable.tech/stackable/pyspark-k8s:3.2.1-hadoop3.2-python39-stackable0.1.0 /stackable/spark/bin/spark-submit --conf spark.hadoop.fs.s3a.aws.credentials.provider=org.apache.hadoop.fs.s3a.AnonymousAWSCredentialsProvider --packages org.apache.hadoop:hadoop-aws:3.2.0,com.amazonaws:aws-java-sdk-s3:1.12.180,com.amazonaws:aws-java-sdk-core:1.12.180 --class tech.stackable.demo.spark.NYTLCReport /repo/target/ny-tlc-report-1.1.0.jar --input /repo/input.txt

# Run on Kubernetes

See examples in the Stackable operator [https://github.com/stackabletech/spark-k8s-operator/blob/main/examples/ny-tlc-report-pvc.yaml](https://github.com/stackabletech/spark-k8s-operator/blob/main/examples/ny-tlc-report-pvc.yaml)
